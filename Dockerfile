FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

COPY pom.xml .
#RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn package -DskipTests -B

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/task_java*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]