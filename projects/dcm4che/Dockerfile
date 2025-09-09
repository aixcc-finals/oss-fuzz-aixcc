FROM ghcr.io/aixcc-finals/base-builder-jvm:v1.3.0

#RUN curl -L https://archive.apache.org/dist/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.zip -o maven.zip && \
COPY pkgs/maven.zip $SRC
RUN unzip maven.zip -d $SRC/maven && \
    rm -rf maven.zip

ENV MVN $SRC/maven/apache-maven-3.6.3/bin/mvn

#RUN git clone --depth 1 https://github.com/dcm4che/dcm4che
COPY pkgs/dcm4che.tar.gz $SRC/
RUN tar -zxf $SRC/dcm4che.tar.gz && rm $SRC/dcm4che.tar.gz

COPY *.sh *.java *.options $SRC/
WORKDIR $SRC/dcm4che
