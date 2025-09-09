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
        tesseract-ocr=4.1.1-2build2 \
        tesseract-ocr-eng=1:4.00~git30-7274cfa-1 \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

RUN mkdir /usr/my-bin && mv /usr/bin/tesseract /usr/my-bin/tesseract

COPY pkgs/maven.zip $SRC/maven.zip
RUN unzip $SRC/maven.zip -d $SRC/maven && rm $SRC/maven.zip

ENV MVN=$SRC/maven/apache-maven-3.9.9/bin/mvn

#RUN git clone --depth 1 https://github.com/google/fuzzing && \
#    cp fuzzing/dictionaries/pdf.dict $SRC/PDFStreamParserFuzzer.dict && \
#    cp fuzzing/dictionaries/pdf.dict $SRC/PDFWriteReadFuzzer.dict && \
#    rm -rf fuzzing \

COPY pkgs/pdf.dict.gz $SRC/pdf.dict.gz
RUN gunzip $SRC/pdf.dict.gz && \
    mv $SRC/pdf.dict $SRC/PDFStreamParserFuzzer.dict && \
    cp $SRC/PDFStreamParserFuzzer.dict $SRC/PDFWriteReadFuzzer.dict

COPY project-parent $SRC/project-parent/

RUN rm -rf $SRC/project-parent/pdfbox
#RUN git clone --depth 1 https://github.com/apache/pdfbox/ $SRC/project-parent/pdfbox

COPY pkgs/pdfbox.tar.gz $SRC/pdfbox.tar.gz
RUN tar -zxf $SRC/pdfbox.tar.gz -C $SRC/project-parent && rm $SRC/pdfbox.tar.gz

COPY build.sh $SRC/
# if not set python infra helper cannot be used for local testing
WORKDIR $SRC/project-parent/pdfbox
