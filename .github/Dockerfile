##########
## RELEASE
FROM adoptopenjdk/openjdk16:jre
WORKDIR /
VOLUME /data
VOLUME /.server-storage
ADD limes.jar /
ENV JAVA_OPTS="-Xmx2G"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/limes.jar"]
