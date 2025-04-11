FROM maven:3.9.9-amazoncorretto-17-alpine AS dependency-builder

ARG REPO_REF=main

RUN addgroup -g 1001 builder && \
    adduser -D -u 1001 -G builder builder && \
    apk add --no-cache --virtual .build-deps \
        git \
        gettext && \
    mkdir -p /build-deps && \
    chown -R builder:builder /build-deps && \
    chmod 700 /build-deps

WORKDIR /build-deps
RUN git config --global advice.detachedHead false && \
    git config --global http.sslVerify true && \
    git config --global gc.auto 0

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

RUN --mount=type=secret,id=maven_username \
    --mount=type=secret,id=maven_password \
    export USERNAME=$(cat /run/secrets/maven_username) && \
    export PASSWORD=$(cat /run/secrets/maven_password) && \
    mkdir -p /home/builder/.m2 && \
    mkdir -p /home/builder/.m2/repository && \
    chmod -R 775 /home/builder/.m2 && \
    envsubst < settings.xml > settings-fixed.xml && \
    mvn -B clean install \
    -s settings-fixed.xml \
    -DskipTests \
    -Dmaven.wagon.http.retryHandler.count=1 \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=30 \
    -Dmaven.artifact.threads=1 \
    -T 1C \
    -Djdk.tls.client.protocols=TLSv1.2

FROM maven:3.9.9-amazoncorretto-17-alpine AS builder

WORKDIR /build
COPY --from=dependency-builder --chown=root:root /root/.m2/repository /root/.m2/repository

COPY .github/settings.xml pom.xml src/ ./

RUN --mount=type=secret,id=maven_username \
    --mount=type=secret,id=maven_password \
    apk add --no-cache --virtual .build-deps gettext && \
    for dir in java resources; do \
      if [ -d "src/test/${dir}" ]; then \
        mkdir -p "src/main/${dir}" && \
        mv "src/test/${dir}"/* "src/main/${dir}/" && \
        rm -rf "src/test/${dir}"; \
      fi; \
    done && \
    mkdir -p /build-artifacts/target && \
    export USERNAME=$(cat /run/secrets/maven_username) && \
    export PASSWORD=$(cat /run/secrets/maven_password) && \
    envsubst < settings.xml > settings-fixed.xml && \
    mvn -B -s settings-fixed.xml \
        -Pdev \
        -Dmaven.test.skip=true \
        -Dmaven.compile.fork=true \
        -Dmaven.repo.local=/root/.m2/repository \
        -Dmaven.javadoc.skip \
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