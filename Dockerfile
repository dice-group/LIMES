########
## BUILD
# build first using maven
FROM maven:3.6.2-jdk-12 as builder
# set workdir
WORKDIR /limes
# copy files
ADD . /limes
WORKDIR /limes/limes-core
RUN mvn clean package shade:shade -Dmaven.test.skip=true
# do some magic to get the right jar file to copy
RUN mvn com.smartcodeltd:release-candidate-maven-plugin:LATEST:version \
-DoutputTemplate="PROJECT_VERSION={{ version }}" \
-DoutputUri="file://\${project.basedir}/__version" && \
cat __version | sed -e /^$/d -e /^#/d -e 's/^/export /' > _version && \
. ./_version && \
cp -p ./target/limes-core-${PROJECT_VERSION}.jar /limes/limes.jar
##########
## RELEASE
# then run in a lighter jdk base
FROM openjdk:12.0.2-jdk
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
