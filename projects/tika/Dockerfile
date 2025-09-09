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

#install tesseract
RUN set -eux \
    && apt-get update \
    && apt-get install --yes --no-install-recommends \
        tesseract-ocr \
        tesseract-ocr-eng \
        exiftool

#move tesseract and exif tool to custom directories
RUN mkdir /usr/my-bin && mv /usr/bin/tesseract /usr/my-bin/tesseract
RUN mkdir /exif && cp /usr/bin/exiftool /exif && chmod -R a+rwx /exif

#RUN curl -L https://archive.apache.org/dist/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.zip -o maven.zip && \
#    unzip maven.zip -d $SRC/maven && \
#    rm -rf maven.zip

COPY pkgs/maven.zip $SRC/maven.zip
RUN unzip $SRC/maven.zip -d $SRC/maven && rm $SRC/maven.zip

ENV MVN=$SRC/maven/apache-maven-3.9.9/bin/mvn

COPY project-parent $SRC/project-parent/

#RUN git clone --depth 1 https://github.com/apache/tika/ $SRC/project-parent/tika

COPY pkgs/tika.tar.gz $SRC/tika.tar.gz
RUN tar -zxf $SRC/tika.tar.gz -C $SRC/project-parent && rm $SRC/tika.tar.gz

COPY build.sh $SRC/
WORKDIR $SRC/project-parent/tika

