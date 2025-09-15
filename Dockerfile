FROM maven:3.9.9-amazoncorretto-17-alpine AS dependency-builder

ARG REPO_REF=main

RUN echo "GITHUB_TESSSTT: ${GITHUB_USERNAME}" \

WORKDIR /build-deps
RUN apk add --no-cache --virtual .build-deps \
        git \
        gettext && \
    mkdir -p /build-deps  && \
    git config --global advice.detachedHead false && \
    git config --global http.sslVerify true && \
    git config --global gc.auto 0 && \
    git clone \
    --depth 1 \
    --branch "${REPO_REF}" \
    --single-branch \
    --filter=blob:none \
    https://github.com/ministryofjustice/payforlegalaid.git && \
    rm -rf /build-deps/payforlegalaid/.git && \
    find /build-deps/payforlegalaid -type f -exec chmod 644 {} \;

WORKDIR /build-deps/payforlegalaid

RUN --mount=type=secret,id=maven_username \
    --mount=type=secret,id=maven_password \
    export USERNAME=$(cat /run/secrets/maven_username) && \
    export PASSWORD=$(cat /run/secrets/maven_password) && \
    mkdir -p /home/builder/.m2 && \
    mkdir -p /home/builder/.m2/repository && \
    chmod -R 775 /home/builder/.m2 && \
    envsubst < .github/settings.xml > settings-fixed.xml && \
    mvn -B clean install \
    -s settings-fixed.xml \
    -DskipTests \
    -Dmaven.wagon.http.retryHandler.count=1 \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=30 \
    -Dmaven.artifact.threads=5 \
    -Djdk.tls.client.protocols=TLSv1.2 && \
    rm settings-fixed.xml

FROM maven:3.9.9-amazoncorretto-17-alpine AS builder

WORKDIR /build
COPY --from=dependency-builder --chown=root:root /root/.m2/repository /root/.m2/repository
COPY .github/settings.xml pom.xml src/ ./

RUN --mount=type=secret,id=maven_username \
    --mount=type=secret,id=maven_password \
    apk add --no-cache --virtual .build-deps gettext && \
    if [ -d "test/java" ]; then \
        mkdir -p src/main/java && \
        mv test/java/* src/main/java/ && \
        rmdir test/java; \
    fi && \
    if [ -d "test/resources" ]; then \
        mkdir -p src/main/resources && \
        mv test/resources/* src/main/resources/ && \
        rmdir test/resources; \
    fi && \
    mkdir -p /build-artifacts/target && \
    export USERNAME=$(cat /run/secrets/maven_username) && \
    export PASSWORD=$(cat /run/secrets/maven_password) && \
    envsubst < settings.xml > settings-fixed.xml && \
    mvn -B -s settings-fixed.xml \
        -Dmaven.repo.local=/root/.m2/repository \
        -Pdev \
        clean package && \
    rm settings-fixed.xml

FROM gcr.io/distroless/java17-debian12
WORKDIR /app

LABEL org.opencontainers.image.authors="GPFD team (laa-payments-finance@digital.justice.gov.uk)" \
      org.opencontainers.image.description="Pay for Legal Aid Application Tests" \
      org.opencontainers.image.vendor="Ministry of Justice" \
      org.opencontainers.image.title="Get Payments & Finance Data" \
      org.opencontainers.image.url="https://github.com/ministryofjustice/payforlegalaid-tests" \
      org.opencontainers.image.documentation="https://github.com/ministryofjustice/payforlegalaid-tests/readme.md" \
      org.opencontainers.image.source="https://github.com/ministryofjustice/payforlegalaid-tests" \
      org.opencontainers.image.licenses="MIT" \
      org.opencontainers.image.base.name="gcr.io/distroless/java17-debian11"

COPY --from=builder --chown=65532:65532 /build/target/payforlegalaid-tests-*.jar app.jar

USER 65532:65532
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "app.jar"]