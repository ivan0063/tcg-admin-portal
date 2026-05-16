# ── Stage 1: Build ─────────────────────────────────────────────
FROM maven:3.9.5-eclipse-temurin-21-alpine AS build
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

# ── Database configuration ──────────────────────────────────────
# Default: H2 in-memory. Override these env vars for PostgreSQL:
#
#   DB_URL=jdbc:postgresql://host:5432/tcgdb
#   DB_USERNAME=tcguser
#   DB_PASSWORD=secret
#   DB_DRIVER=org.postgresql.Driver
#   JPA_DDL_AUTO=update
#   H2_CONSOLE_ENABLED=false
# ────────────────────────────────────────────────────────────────
ENV DB_URL=jdbc:h2:mem:tcgdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
ENV DB_USERNAME=sa
ENV DB_PASSWORD=
ENV DB_DRIVER=org.h2.Driver
ENV JPA_DDL_AUTO=create-drop
ENV H2_CONSOLE_ENABLED=true
ENV SERVER_PORT=8080

ENTRYPOINT ["java", "-jar", "app.jar"]
