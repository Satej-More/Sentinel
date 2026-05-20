# Stage 1: Build the application using JDK and wrapper
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copy Maven wrapper and configuration
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Ensure line endings are LF for the wrapper and run permissions are set
RUN tr -d '\r' < mvnw > mvnw.tmp && mv mvnw.tmp mvnw && chmod +x mvnw

# Download dependencies (this layer will be cached unless pom.xml changes)
RUN ./mvnw dependency:go-offline -B

# Copy application source code and compile
COPY src src
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Runtime environment with JRE
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Run the container under a secure, non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy the built jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Configure JVM flags for efficient resource utilization in containers
ENV JAVA_OPTS="-XX:+UseG1GC -XX:+UseStringDeduplication"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
