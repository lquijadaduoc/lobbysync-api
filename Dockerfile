# Build stage
FROM eclipse-temurin:17-jdk as builder

WORKDIR /build

# Copiar archivos del proyecto
COPY pom.xml .
COPY src ./src

# Compilar con Maven
RUN apt-get update && apt-get install -y maven && \
    mvn clean package -DskipTests -q

# Runtime stage
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copiar JAR compilado del build stage
COPY --from=builder /build/target/*.jar app.jar

# Copiar serviceAccountKey.json si existe
COPY serviceAccountKey.json* /app/

# Crear directorio para config
RUN mkdir -p /app/config

EXPOSE 8080

ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-jar", "app.jar"]


