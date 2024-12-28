#FROM openjdk:18-alpine
#COPY target/*.jar app.jar
#ENTRYPOINT ["java","-jar","/app.jar"]

FROM openjdk:18-alpine


RUN apk --no-cache add curl vim
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
