########
## BUILD
# build first using maven
FROM maven:3.8-adoptopenjdk-16 as builder
WORKDIR /limes
ADD . /limes
WORKDIR /limes/limes-core/src/main/resources/wordnet
RUN curl https://wordnetcode.princeton.edu/3.0/WordNet-3.0.tar.gz --output WordNet-3.0.tar.gz && \
    tar -xzf WordNet-3.0.tar.gz && \
    rm WordNet-3.0.tar.gz && \
    mv WordNet-3.0/dict . && \
    rm -rf WordNet-3.0 && \
    curl https://wordnetcode.princeton.edu/wn3.1.dict.tar.gz --output wn3.1.dict.tar.gz && \
    tar -xzf wn3.1.dict.tar.gz && \
    rm wn3.1.dict.tar.gz
WORKDIR /limes
RUN mvn clean package shade:shade -Dmaven.test.skip=true
WORKDIR /limes/limes-core
RUN PROJECT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout) && \
    cp -p ./target/limes-core-${PROJECT_VERSION}.jar /limes/limes.jar
##########
## RELEASE
# then run in a lighter jdk base
FROM adoptopenjdk/openjdk16:jre
WORKDIR /
VOLUME /data
# copy jar from build step
COPY --from=builder /limes/limes.jar limes.jar
ENV JAVA_OPTS="-Xmx2G"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/limes.jar"]
