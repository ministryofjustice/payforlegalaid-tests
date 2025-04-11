FROM maven:3.9.9-amazoncorretto-17-alpine AS dependency-builder

ARG REPO_REF=main

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
    -Djdk.tls.client.protocols=TLSv1.2

FROM maven:3.9.9-amazoncorretto-17-alpine AS builder

WORKDIR /build
COPY --from=dependency-builder --chown=root:root /root/.m2/repository /root/.m2/repository
COPY .github/settings.xml pom.xml src/ ./

RUN --mount=type=secret,id=maven_username \
    --mount=type=secret,id=maven_password \
    apk add --no-cache --virtual .build-deps gettext && \
    if [ -d "src/test/java" ]; then \
        mkdir -p src/main/java && \
        mv src/test/java/* src/main/java/ && \
        rm -rf src/test/java; \
    fi && \
    if [ -d "src/test/resources" ]; then \
        mkdir -p src/main/resources && \
        mv src/test/resources/* src/main/resources/ && \
        rm -rf src/test/resources; \
    fi && \
    mkdir -p /build-artifacts/target && \
    export USERNAME=$(cat /run/secrets/maven_username) && \
    export PASSWORD=$(cat /run/secrets/maven_password) && \
    ls -alp && \
    echo "FPPPO" && \
    ls -alp src && \
    envsubst < settings.xml > settings-fixed.xml && \
    mvn -B -s settings-fixed.xml \
        -Dmaven.repo.local=/root/.m2/repository \
        clean package

FROM maven:3.9.9-amazoncorretto-17-alpine
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

RUN echo -e '#!/bin/sh\n\
while :; do \n\
  echo "Hello World" \n\
  sleep 5 \n\
done' > /entrypoint.sh && \
chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]
