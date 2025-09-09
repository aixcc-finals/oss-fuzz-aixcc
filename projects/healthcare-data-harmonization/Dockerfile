FROM ghcr.io/aixcc-finals/base-builder-jvm:v1.3.0

#RUN curl -L https://services.gradle.org/distributions/gradle-7.6-bin.zip -o gradle.zip && \
COPY pkgs/gradle.zip $SRC/
RUN unzip gradle.zip -d $SRC/gradle && \
    rm -rf gradle.zip

ENV GRADLE $SRC/gradle/gradle-7.6/bin/gradle

#RUN git clone --depth 1 https://github.com/GoogleCloudPlatform/healthcare-data-harmonization
COPY pkgs/healthcare-data-harmonization.tar.gz $SRC/
RUN tar -zxf $SRC/healthcare-data-harmonization.tar.gz && rm $SRC/healthcare-data-harmonization.tar.gz
WORKDIR $SRC/healthcare-data-harmonization
COPY build.sh *.options $SRC/
COPY *Fuzzer.java $SRC/
