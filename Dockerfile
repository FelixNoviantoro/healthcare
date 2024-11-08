FROM openjdk:17-jdk-alpine
ARG JAR_FILE=target/api-core-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} application.jar
ENTRYPOINT ["java", "-jar", "application.jar"]
EXPOSE 8081