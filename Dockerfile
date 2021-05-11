########
## BUILD
# build first using maven
FROM maven:3.8-adoptopenjdk-16 as builder
WORKDIR /limes
ADD . /limes
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
