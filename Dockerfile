## BUILD
# build first using maven
FROM maven:3.3.9-jdk-8 as builder
# set workdir
WORKDIR /limes
# install deps
RUN apt-get update && apt-get install -y openjfx
# copy files
ADD . /limes
# install deps
RUN cd limes-core && mvn install -U -Dmaven.test.skip=true
# run tests
RUN cd limes-core && mvn test
# build package
RUN cd limes-core && mvn clean package shade:shade -Dmaven.test.skip=true
# copy final app jar
RUN cp -p $(find limes-core/target -name 'limes-core-*SNAPSHOT.jar') limes.jar

## RELEASE
# then run in a lighter jdk base
FROM openjdk:8-jdk
# set workdir
WORKDIR /limes
# copy jar from build step
COPY --from=builder /limes/limes.jar limes.jar
# set default java flags
ENV JAVA_OPTS="-Xmx2G"
# expose port
EXPOSE 8080
# assign start command
CMD ["java", "-jar", "limes.jar", "-s"]
