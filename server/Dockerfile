# Referenced from gh.com/lucko/bytebin
FROM eclipse-temurin:25-alpine

RUN addgroup -S version && adduser -S -G version version
USER version

WORKDIR /opt/version
COPY server/build/libs/VersionServer.jar ./VersionServer.jar

HEALTHCHECK --interval=1m --timeout=5s \
    CMD wget http://localhost:8080/api/v1/health -q -O - | grep -c '{"status":"ok"}' || exit 1

ENTRYPOINT ["java", "-jar", "VersionServer.jar"]
EXPOSE 8080
