# === Build Stage ===
FROM maven:3.9.9-eclipse-temurin-21-noble AS builder

# Working directory
WORKDIR /app

# Copy and install dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy `src` to container's directory
COPY src ./src

# Build jar
RUN mvn clean package -DskipTests

# === Runtime Stage ===
# Base image
FROM eclipse-temurin:21-jre-noble

# Working directory
WORKDIR /app

# Copy Spring Boot jar into container
COPY --from=builder app/target/*.jar /app/app.jar

# Expose Tomcat Server
EXPOSE 8080

# Run application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
