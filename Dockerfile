# Multi-stage Docker build for Class Service

# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy all POM files
COPY parent/pom.xml /app/parent/pom.xml
COPY class-microservice/pom.xml /app/class-microservice/pom.xml

# Install parent POM
RUN cd /app/parent && mvn install -N

# Download microservice dependencies
RUN mkdir -p /app/class-microservice/src/main/java/temp && \
    echo "public class Temp {}" > /app/class-microservice/src/main/java/temp/Temp.java

RUN cd /app/class-microservice && mvn dependency:go-offline -DskipTests

# Clean temp files
RUN rm -rf /app/class-microservice/src/main/java/temp

# Build class service
COPY class-microservice/src /app/class-microservice/src
RUN cd /app/class-microservice && mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/class-microservice/target/class-microservice-*.jar app.jar

# Expose port
EXPOSE 8084

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
