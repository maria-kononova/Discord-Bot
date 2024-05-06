FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /usr/src/app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src/ /usr/src/app/src/
RUN mvn package

FROM openjdk:17
COPY --from=build /usr/src/app/target/Bot-0.0.1-SNAPSHOT.jar Bot.jar
CMD ["java", "-jar", "Bot.jar"]