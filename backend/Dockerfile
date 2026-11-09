# Multi-stage Dockerfile for FinPath Backend (Spring Boot 3.3.4 + Java 21)
FROM gradle:8.8-jdk21-alpine AS builder

WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . .
RUN gradle bootJar --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create non-root user for security
RUN addgroup -S finpath && adduser -S finpath -G finpath

# Document storage volume
RUN mkdir -p /app/storage/documents && chown -R finpath:finpath /app

COPY --from=builder --chown=finpath:finpath /home/gradle/src/build/libs/*.jar app.jar

USER finpath

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=docker \
    JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

