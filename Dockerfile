# ──────────────────────────────────────────────────────────────────────────────
# Stage 1 — Build
#   Uses full Maven + JDK image to compile and package the application.
#   Dependencies are cached in a separate layer for faster re-builds.
# ──────────────────────────────────────────────────────────────────────────────
FROM maven:3.9.5-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml first and download dependencies (this layer is cached)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build the fat JAR
COPY src ./src
RUN mvn clean package -DskipTests -B

# ──────────────────────────────────────────────────────────────────────────────
# Stage 2 — Run
#   Uses lightweight JRE-only Alpine image (~85 MB vs ~500 MB for full JDK).
#   Runs as a non-root user for security best practice.
# ──────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create a non-root user
RUN addgroup -S spring && adduser -S spring -G spring

# Copy only the final JAR from the builder stage
COPY --from=builder /app/target/employee-api-0.0.1-SNAPSHOT.jar app.jar

# Switch to non-root user
USER spring:spring

# Expose port
EXPOSE 8080

# Health check — polls /actuator/health every 30s
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

# Start the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
