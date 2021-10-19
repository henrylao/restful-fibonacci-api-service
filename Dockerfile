FROM openjdk:8-jdk-alpine
MAINTAINER hcl.henry.lao
ADD target/restful-fibonacci-api-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/message-server-1.0.0.jar"]

