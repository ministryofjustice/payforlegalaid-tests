FROM maven:3.9.9-amazoncorretto-17-alpine AS dependency-builder

RUN addgroup -g 1001 builder && \
    adduser -D -u 1001 -G builder builder && \
    apk add --no-cache --virtual .build-deps \
        git \
        gettext && \
    mkdir -p /build-deps && \
    chown -R builder:builder /build-deps && \
    chmod 700 /build-deps

WORKDIR /build-deps
USER builder

RUN mkdir -p /home/builder/.m2 && \
    chmod -R 755 /home/builder && \
    git config --global advice.detachedHead false && \
    git config --global http.sslVerify true && \
    git config --global gc.auto 0

ARG REPO_REF=main
RUN git clone \
    --depth 1 \
    --branch "${REPO_REF}" \
    --single-branch \
    --filter=blob:none \
    https://github.com/ministryofjustice/payforlegalaid.git && \
    rm -rf /build-deps/payforlegalaid/.git && \
    find /build-deps/payforlegalaid -type f -exec chmod 644 {} \;

WORKDIR /build-deps/payforlegalaid
COPY .github/settings.xml .
ARG USERNAME
ARG PASSWORD
ENV USERNAME=$USERNAME \
    PASSWORD=$PASSWORD

RUN envsubst < settings.xml > settings-fixed.xml && \
    mkdir -p /home/builder/.m2/repository && \
    chmod -R 775 /home/builder/.m2

RUN mvn -B clean install \
    -s settings-fixed.xml \
    -DskipTests \
    -Dmaven.wagon.http.retryHandler.count=1 \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=30 \
    -Dmaven.artifact.threads=1 \
    -T 1C \
    -Djdk.tls.client.protocols=TLSv1.2

FROM maven:3.9.9-amazoncorretto-17-alpine AS builder

USER root
RUN adduser -D -u 1000 mavenuser && \
    mkdir -p /home/mavenuser/.m2 && \
    chown -R mavenuser:mavenuser /home/mavenuser && \
    chmod -R 755 /home/mavenuser

WORKDIR /build
COPY --from=dependency-builder --chown=mavenuser:mavenuser /home/builder/.m2/repository /home/mavenuser/.m2/repository
RUN chown -R mavenuser:mavenuser /home/mavenuser/.m2 && \
    chmod -R 775 /home/mavenuser/.m2

COPY --chown=mavenuser:mavenuser .github/settings.xml .

COPY --chown=mavenuser:mavenuser pom.xml .
COPY --chown=mavenuser:mavenuser src ./src

RUN if [ -d "src/test/java" ]; then \
      mkdir -p src/main/java && \
      mv src/test/java/* src/main/java/ && \
      rm -rf src/test/java && \
      chmod -R 750 src/main/java; \
    fi \
    && if [ -d "src/test/resources" ]; then \
      mkdir -p src/main/resources && \
      mv src/test/resources/* src/main/resources/ && \
      rm -rf src/test/resources && \
      chmod -R 640 src/main/resources; \
    fi

RUN chown -R mavenuser:mavenuser /build && \
    chmod -R 775 /build && \
    mkdir -p /build-artifacts/target && \
    chown -R 65532:65532 /build-artifacts && \
    chmod -R 775 /build-artifacts/target

USER mavenuser
RUN mvn -B -s settings.xml -Duser.home=/home/mavenuser \
    -Pdev \
    -Dmaven.test.skip=true \
    -Dmaven.artifact.threads=5 \
    -Djdk.tls.client.protocols=TLSv1.2 \
    clean package

FROM gcr.io/distroless/java17-debian11
WORKDIR /app

COPY --from=builder --chown=65532:65532 /build-artifacts/target /app/reports
COPY --from=builder --chown=65532:65532 /build-artifacts/target /app/target
COPY --from=builder --chown=65532:65532 /build/target/payforlegalaid-tests-*.jar app.jar

ENV REPORT_DIR=/app/reports

USER 65532:65532
ENTRYPOINT ["java", "-jar", "app.jar"]