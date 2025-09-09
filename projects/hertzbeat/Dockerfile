FROM ghcr.io/aixcc-finals/base-builder-jvm:v1.3.0

#RUN curl -L https://archive.apache.org/dist/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.zip -o maven.zip && \
COPY pkgs/maven.zip $SRC/
RUN unzip maven.zip -d $SRC/maven && \
    rm -rf maven.zip
ENV MVN $SRC/maven/apache-maven-3.6.3/bin/mvn
#RUN git clone --depth 1 https://github.com/apache/hertzbeat
COPY pkgs/hertzbeat.tar.gz $SRC/
COPY pkgs/submodules.tar.gz $SRC/aixcc-submodules/
RUN tar -zxf $SRC/hertzbeat.tar.gz && rm $SRC/hertzbeat.tar.gz
WORKDIR $SRC/hertzbeat
COPY build.sh *.options $SRC/
COPY *Fuzzer.java $SRC/
