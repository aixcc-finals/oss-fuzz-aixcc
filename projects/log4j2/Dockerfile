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

FROM ghcr.io/aixcc-finals/base-builder-jvm:v1.3.0

COPY pkgs/maven.zip $SRC/

RUN unzip maven.zip -d "$SRC/maven-3.6.3" && rm -rf maven.zip

ENV MVN=$SRC/maven-3.6.3/apache-maven-3.6.3/bin/mvn

COPY fuzz-targets $SRC/fuzz-targets

COPY pkgs/logging-log4j2.tar.gz $SRC/logging-log4j2.tar.gz
RUN tar -zxf $SRC/logging-log4j2.tar.gz -C $SRC && rm $SRC/logging-log4j2.tar.gz

ADD pkgs/OpenJDK8U-jdk_x64_linux_hotspot_8u432b06.tar.gz $SRC/

ENV JAVA_HOME=$SRC/jdk8u432-b06

COPY build.sh *.options $SRC/
WORKDIR $SRC/logging-log4j2

