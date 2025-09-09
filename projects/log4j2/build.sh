#!/bin/bash -eu
# Copyright 2023 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
################################################################################

PROJECT=logging-log4j2
#MAIN_REPOSITORY=https://github.com/apache/logging-log4j2

MAVEN_ARGS="-DskipTests -Dspotless.skip -Drat.skip -Dspotbugs.skip -Dossindex.skip"

function set_project_version_in_fuzz_targets_dependency {
    PROJECT_VERSION=$(cd $SRC/$PROJECT && $MVN org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -Dexpression=project.version -q -DforceStdout)
    # set dependency project version in fuzz-targets
    (cd $SRC/fuzz-targets && $MVN versions:use-dep-version -Dexcludes=com.code-intelligence:jazzer -DdepVersion="$PROJECT_VERSION" -DforceVersion=true)
}
die() { echo "$*" 1>&2 ; exit 1; }

cp ${SRC}/*.options ${OUT}/

set_project_version_in_fuzz_targets_dependency

#clean out repo
rm -rf $OUT/m2

#build each locally
cd "$SRC"/"$PROJECT" && $MVN clean install $MAVEN_ARGS -am -pl :log4j-api,:log4j-core -Dmaven.repo.local=$OUT/m2

cd "$SRC"/fuzz-targets && $MVN clean package -Dmaven.repo.local=$OUT/m2

# build classpath
cp "$SRC"/"$PROJECT"/api/target/log4j-api-"${PROJECT_VERSION}".jar "$OUT"/log4j2-api.jar || die "can't copy api jar"
cp "$SRC"/"$PROJECT"/core/target/log4j-core-"${PROJECT_VERSION}".jar "$OUT"/log4j2-core.jar || die "can't copy core jar"
cp "$SRC"/fuzz-targets/target/fuzz-targets-0.0.1-SNAPSHOT.jar "$OUT"/fuzz-targets.jar || die "can't copy fuzz targets jar"
cp "$SRC"/fuzz-targets/log4j2.xml "$OUT"/log4j2.xml || die "can't copy log4j2.xml"

RUNTIME_CLASSPATH_ABSOLUTE="$OUT/log4j2-api.jar:$OUT/log4j2-core.jar:$OUT/fuzz-targets.jar"

# replace $OUT with placeholder $this_dir that will be dissolved at runtime
# shellcheck disable=SC2001
RUNTIME_CLASSPATH=$(echo "$RUNTIME_CLASSPATH_ABSOLUTE" | sed "s|$OUT|\$this_dir|g")

readarray -d '' fuzzers < <(find "$SRC"/fuzz-targets -name '*Fuzzer.java' -print0)

for fuzzer in "${fuzzers[@]}"; do
     fuzzer_basename=$(basename -s .java "$fuzzer")

     # Create an execution wrapper for every fuzztarget
     echo "#!/bin/bash
    # LLVMFuzzerTestOneInput comment for fuzzer detection by infrastructure.
    this_dir=\$(dirname \"\$0\")
    LD_LIBRARY_PATH=\"$JVM_LD_LIBRARY_PATH\":\$this_dir \
    \$this_dir/jazzer_driver --agent_path=\$this_dir/jazzer_agent_deploy.jar \
    --cp=$RUNTIME_CLASSPATH \
    --target_class=com.example.$fuzzer_basename \
    --jvm_args=\"-Dlog4j.configurationFile=\$this_dir/log4j2.xml\" \
    --instrumentation_includes=\"com.**:org.**\" \
    \$@" > "$OUT"/"$fuzzer_basename"
      chmod +x "$OUT"/"$fuzzer_basename"
done
