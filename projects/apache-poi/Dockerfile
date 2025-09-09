# Copyright 2022 Google LLC
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

WORKDIR $SRC

COPY pkgs/maven.zip $SRC/

RUN unzip maven.zip -d "$SRC/maven-3.6.3" && rm -rf maven.zip

ENV MVN=$SRC/maven-3.6.3/apache-maven-3.6.3/bin/mvn
ENV TARGET_PACKAGE_PREFIX=org.apache.poi.*:org.apache.xmlbeans.*

# Newer JDK releases are listed at https://github.com/adoptium/temurin8-binaries/releases/
COPY pkgs/OpenJDK8U-jdk_x64_linux_hotspot_8u432b06.tar.gz $SRC/
RUN tar xvf OpenJDK8U-jdk_x64_linux_hotspot_8u432b06.tar.gz && \
  rm -rf OpenJDK8U-jdk_x64_linux_hotspot_8u432b06.tar.gz

ENV JAVA_HOME_8=$SRC/jdk8u432-b06

# Newer JDK releases are listed at https://github.com/adoptium/temurin11-binaries/releases/
COPY pkgs/OpenJDK11U-jdk_x64_linux_hotspot_11.0.25_9.tar.gz $SRC/
RUN tar xvf OpenJDK11U-jdk_x64_linux_hotspot_11.0.25_9.tar.gz && \
  rm -rf OpenJDK11U-jdk_x64_linux_hotspot_11.0.25_9.tar.gz

ENV JAVA_HOME_11=$SRC/jdk-11.0.25+9

#
# clone repository
#
COPY pkgs/apache-poi.tar.gz $SRC/
RUN tar -xzvf apache-poi.tar.gz && \
    mv apache-poi poi && rm apache-poi.tar.gz

# install packages required for font-handling and other code in java.awt.*
RUN apt-get update \
    && apt-get install -y  \
    fontconfig=2.13.1-2ubuntu3 \
    libfreetype6=2.10.1-2ubuntu0.4  \
    libxext6=2:1.3.4-0ubuntu1  \
    libx11-6=2:1.6.9-2ubuntu1.6 \
    libxrender1=1:0.9.10-1  \
    libxtst6=2:1.2.3-1 \
    libxi6=2:1.7.10-0ubuntu1 \
    libxcb1=1.14-2 \
    libxau6=1:1.0.9-0ubuntu1 \
    libxdmcp6=1:1.1.3-0ubuntu1 --no-install-recommends \
&& apt-get clean autoclean \
&& apt-get autoremove --yes \
&& rm -rf /var/lib/apt && rm -rf var/lib/dpkg && rm -rf /var/lib/cache && rm -rf /var/lib/log

COPY pom.xml build.sh *.options $SRC/
COPY src/ $SRC/src/
WORKDIR $SRC/poi
