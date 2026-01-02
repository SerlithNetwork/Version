
FROM gradle:jdk21-corretto AS build-project

WORKDIR /version
COPY gradle/ ./gradle/
COPY src/ ./src/
COPY build.gradle.kts ./
COPY gradlew ./
COPY gradlew.bat ./
COPY settings.gradle.kts ./
RUN chmod +x gradlew && ./gradlew build --no-daemon --stacktrace

FROM eclipse-temurin:25-alpine

RUN addgroup -S version && adduser -S -G version version
USER version

WORKDIR /opt/version
COPY --from=build-project /version/build/libs/Version-0.0.1-SNAPSHOT.jar ./version.jar

RUN mkdir pictures config
VOLUME ["/opt/version/config", "/opt/version/data"]

HEALTHCHECK --interval=1m --timeout=5s \
    CMD wget http://localhost:9450/api/health -q -O - | grep -c '{"status":"ok"}' || exit 1

CMD ["java", "-jar", "version.jar"]
EXPOSE 9450/tcp
