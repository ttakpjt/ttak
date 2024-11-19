FROM --platform=linux/amd64 azul/zulu-openjdk:21-latest
LABEL authors="chris2769"

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
