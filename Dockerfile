FROM maven:3.8-openjdk-17 AS builder

WORKDIR /app

COPY pom.xml .

RUN mvn clean dependency:resolve dependency:resolve-plugins

RUN mvn org.apache.maven.plugins:maven-dependency-plugin:3.1.2:get \
    -Dartifact=org.projectlombok:lombok:1.18.36:jar

COPY src ./src

RUN mvn clean package -DskipTests


FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar
COPY serviceAccountKey.json .

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

