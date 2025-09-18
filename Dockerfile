FROM maven:3.9.9-amazoncorretto-17-alpine AS dependency-builder

ARG REPO_REF=LPF-1043-v4
ARG REPO_REF_OPEN=v0.0.6

WORKDIR /build-deps
RUN echo "inside a build repo"
RUN pwd && ls -al

RUN apk add --no-cache --virtual .build-deps \
        git \
        curl \
        gettext && \
    mkdir -p /build-deps  && \
    git config --global advice.detachedHead false && \
    git config --global http.sslVerify true && \
    git config --global gc.auto 0 && \
    git clone \
    --depth 1 \
    --branch "${REPO_REF_OPEN}" \
    --single-branch \
    --filter=blob:none \
    https://github.com/ministryofjustice/payforlegalaid-openapi && \
    rm -rf /build-deps/payforlegalaid-openapi/.git && \
    find /build-deps/payforlegalaid-openapi -type f -exec chmod 644 {} \;


RUN apk add --no-cache --virtual .build-deps \
        git \
        curl \
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

RUN echo "finished build step for payforlegalaid"
RUN pwd && ls -al

WORKDIR /build-deps/payforlegalaid

RUN echo "Current directory inside build-deps/payforlegalaid:" && pwd && echo "Files:" && ls -al

RUN --mount=type=secret,id=maven_username \
    --mount=type=secret,id=maven_password \
    export USERNAME="$(cat /run/secrets/maven_username)" && \
    export PASSWORD="$(cat /run/secrets/maven_password)" && \
    mkdir -p /home/builder/.m2 && \
    mkdir -p /home/builder/.m2/repository && \
    chmod -R 775 /home/builder/.m2 && \
    mvn clean install -DfinalName=payforlegalaid-openapi-0.0.6 \
    -DskipTests \
    -Dmaven.artifact.threads=5 \
    -Djdk.tls.client.protocols=TLSv1.2


RUN --mount=type=secret,id=maven_username \
    --mount=type=secret,id=maven_password \
    export USERNAME="$(cat /run/secrets/maven_username)" && \
    export PASSWORD="$(cat /run/secrets/maven_password)" && \
    mkdir -p /home/builder/.m2 && \
    mkdir -p /home/builder/.m2/repository && \
    chmod -R 775 /home/builder/.m2 && \
    cp .github/settings.xml /home/builder/.m2/settings.xml && \
    cp .github/settings.xml /home/builder/.m2/repository/settings.xml && \
    mvn -B -e -X clean install \
    -s .github/settings.xml \
    -DskipTests \
    -Dmaven.artifact.threads=5 \
    -Djdk.tls.client.protocols=TLSv1.2

RUN echo "After mvn -B clean package"
RUN pwd && ls -al


FROM maven:3.9.9-amazoncorretto-17-alpine AS builder

WORKDIR /build
COPY --from=dependency-builder --chown=root:root /root/.m2/repository /root/.m2/repository

COPY pom.xml src/ ./

RUN --mount=type=secret,id=maven_username \
    --mount=type=secret,id=maven_password \
    apk add --no-cache --virtual .build-deps gettext && \
    mkdir -p src/main/java && \
    mv test/java/* src/main/java/ && \
    mkdir -p src/main/resources && \
    mv test/resources/* src/main/resources/ && \
    mkdir -p /build-artifacts/target && \
    export USERNAME="$(cat /run/secrets/maven_username)" && \
    export PASSWORD="$(cat /run/secrets/maven_password)" && \
    mvn -B -X \
        -Dmaven.repo.local=/root/.m2/repository \
        clean package


#FROM gcr.io/distroless/java17-debian12
#WORKDIR /app
#
#LABEL org.opencontainers.image.authors="GPFD team (laa-payments-finance@digital.justice.gov.uk)" \
#      org.opencontainers.image.description="Pay for Legal Aid Application Tests" \
#      org.opencontainers.image.vendor="Ministry of Justice" \
#      org.opencontainers.image.title="Get Payments & Finance Data" \
#      org.opencontainers.image.url="https://github.com/ministryofjustice/payforlegalaid-tests" \
#      org.opencontainers.image.documentation="https://github.com/ministryofjustice/payforlegalaid-tests/readme.md" \
#      org.opencontainers.image.source="https://github.com/ministryofjustice/payforlegalaid-tests" \
#      org.opencontainers.image.licenses="MIT" \
#      org.opencontainers.image.base.name="gcr.io/distroless/java17-debian11"
#
#COPY --from=builder --chown=65532:65532 /build/target/payforlegalaid-tests-*.jar app.jar
#
#USER 65532:65532
#ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "app.jar"]