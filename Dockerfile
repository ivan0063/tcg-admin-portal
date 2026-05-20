# ── Stage 1: Build ─────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /build

# Cache dependencies layer separately
COPY pom.xml .
RUN mvn dependency:go-offline -q

COPY src ./src
RUN mvn package -DskipTests -q

# ── Stage 2: Runtime ────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S tcg && adduser -S tcg -G tcg
USER tcg

COPY --from=build /build/target/*.jar app.jar

EXPOSE 8080

# Inject DB_URL, DB_USERNAME, DB_PASSWORD, DB_DRIVER, JPA_DDL_AUTO at runtime.
# Local/dev default: H2 file-based (set in application.yml property placeholders).
ENV SERVER_PORT=8080

ENTRYPOINT ["java", "-jar", "app.jar"]
