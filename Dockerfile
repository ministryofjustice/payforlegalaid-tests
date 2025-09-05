# Stage 1: Dependency Resolution
# Start off with alpine linux, and the amazon distribution of Java 17
FROM maven:3.9.9-amazoncorretto-17-alpine AS dependency-builder

# We pass in a branch ref, but default to main
ARG REPO_REF=main

# Do stuff in the /build-deps folder
WORKDIR /build-deps

# HIGH LEVEL: This step is about checking out the code
#
# apk add is installing software packages in alpine (the image of linux being used)
# Virtual package so can easily remove at end and shrink image size
# No-cache so package index isn't stored => smaller image size
# Note: We don't actually remove the virtual package .build-deps at the end of each stage, because the new stage means they won't be copied into the final image anyway
RUN apk add --no-cache --virtual .build-deps \
# git needed to checkout code
        git \
#         gettext is for internationalisation (i18n) and localisation (l10n) of packages
#         Not sure if this is a hard dependency for us - it might be some other steps require it
        gettext && \
#         Create folder if not already there
    mkdir -p /build-deps  && \
#     Turn off git warnings about detachedHead
    git config --global advice.detachedHead false && \
#     Ensures we verify SSL certificates when using https
    git config --global http.sslVerify true && \
#     disables "automatic packing"
    git config --global gc.auto 0 && \
    git clone \
#     Get only the last commit of the code and not all the history
    --depth 1 \
#     Use main (or whatever branch was passed in)
    --branch "${REPO_REF}" \
#  only get the details of this branch and its past, not all the other branches
    --single-branch \
#     Filter out all file contents (blobs) until needed by git
    --filter=blob:none \
#     our repository!
    https://github.com/ministryofjustice/payforlegalaid.git && \
# Cleanup unnecessary history etc to reduce image size
    rm -rf /build-deps/payforlegalaid/.git && \
#     Sets all the files we just checked out to have owner: rw, group: r, others: r
    find /build-deps/payforlegalaid -type f -exec chmod 644 {} \;

# Switch directory to the just checked out code
WORKDIR /build-deps/payforlegalaid

# HIGH LEVEL: We prepare for the clean install by setting up the PAT to access OPEN-API
# @Athar - this is probably the issue since we don't get Open API that way??
#
# Sets the maven_username as a secret, and so not baked into the image. mounts it as a file in /run/secrets
# The maven_username is passed into the docker build command
RUN --mount=type=secret,id=maven_username \
# Sets the maven_password as a secret, and so not baked into the image. mounts it as a file in /run/secrets
# The maven_password is passed into the docker build command
    --mount=type=secret,id=maven_password \
#     gets the just mounted username and sets it as an env variable. This isn't used anymore?
    export USERNAME=$(cat /run/secrets/maven_username) && \
    #     gets the just mounted password and sets it as an env variable. This isn't used anymore?
    export PASSWORD=$(cat /run/secrets/maven_password) && \
#     Create a maven directory if not existing already
    mkdir -p /home/builder/.m2 && \
#     Create an maven  repository if not already existing
    mkdir -p /home/builder/.m2/repository && \
#     Change permissions of maven folder so owner: rwx, group: rwx, others: rw
    chmod -R 775 /home/builder/.m2 && \
#     Replace env variable placeholders in the .github/settings.xml from the /payforlegalaid repo - so the idea was to set our PAT token to access the Open API repo
#     Note that username and password are no longer defined in .github/settings.xml, it will instead just set ${env.HOME} if that is defined here
#     Save this new settings file in settings-fixed.xml
    envsubst < .github/settings.xml > settings-fixed.xml && \
# Run maven in batch mode - disables output colour and takes default options if user input is needed
# Build the code into a jar and install that in the maven repository
    mvn -B clean install \
#   use the settings file populated a few lines ago
    -s settings-fixed.xml \
#     Don't run the tests as part of the build
    -DskipTests \
#    Try one retry if the first attempt to fetch remote dependencies fails
    -Dmaven.wagon.http.retryHandler.count=1 \
#     Only spend 30 seconds trying to get remote dependencies
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=30 \
#     Use 5 threads for downloading remote dependencies
    -Dmaven.artifact.threads=5 \
# Force to use TLS1.2, which is more secure. older versions of TLS are deprecated, but some versions of java may default to them
    -Djdk.tls.client.protocols=TLSv1.2 && \
#     Now we've finished installing, we can remove the version of settings containing the username/password
    rm settings-fixed.xml

# Stage 2: Building the code
# The idea here is basically that if the dependencies (i.e. payforlegalaid code) doesn't change, docker will reuse that stage of the image
# and only rebuild this stage
FROM maven:3.9.9-amazoncorretto-17-alpine AS builder

WORKDIR /build
# This copies from our earlier layer all the maven dependencies, over to our current stage
COPY --from=dependency-builder --chown=root:root /root/.m2/repository /root/.m2/repository
# Copy the github settings, pom and src code from the calling context
# This dockerfile is built in the payforlegalaid's deploy_acceptance_tests_to_env.yml workflow.
# Before calling it checks out the payforlegalaid-test repo, so the settings, pom and src code being copied to the docker image here is the test code
COPY .github/settings.xml pom.xml src/ ./

# As before, mount the username and password passed into the build command
RUN --mount=type=secret,id=maven_username \
    --mount=type=secret,id=maven_password \
#   We want to install gettext. As above, it is designed for i18n and I'm not sure its role here
# As before, we avoid caching to save image space
# As before, we use a virtual grouping to make it easier to clean up at the end - but since we are using Stages we don't need to expliclity delete it to stop it ending up in final image
    apk add --no-cache --virtual .build-deps gettext && \
#     We check if there is a /test/java folder in the acceptance test code
    if [ -d "test/java" ]; then \
#   If there is (there should be!), it will make a src java folder, move the test code to that, and then remove the test code folder
        mkdir -p src/main/java && \
        mv test/java/* src/main/java/ && \
        rmdir test/java; \
    fi && \
#   We check if there is a /test/resources folder in the acceptance test code
    if [ -d "test/resources" ]; then \
#   If there is (there should be!), it will make a src resources folder, move the test code to that, and then remove the test code folder
        mkdir -p src/main/resources && \
        mv test/resources/* src/main/resources/ && \
        rmdir test/resources; \
    fi && \
# Not sure why we are making this yet
    mkdir -p /build-artifacts/target && \
    # As before, save the username and password as env variables
#     As before, these aren't used anymore...
    export USERNAME=$(cat /run/secrets/maven_username) && \
    export PASSWORD=$(cat /run/secrets/maven_password) && \
#     Stick the env variables in the settings.xml
# As before, the username and password are no longer defined in settings.xml so it will only be env.HOME set
# Save the updated settings.xml into settings-fixed.xml
    envsubst < settings.xml > settings-fixed.xml && \
#     Do a maven build in batch mode with the specified settings
    mvn -B -s settings-fixed.xml \
#     Use the depedencies we set up in Stage 1
        -Dmaven.repo.local=/root/.m2/repository \
#         Use the dev profile - this is for application.yml settings etc
        -Pdev \
#         Package - so don't install to local maven, just make a jar.
        clean package && \
#         Clean up the settings.xml we created earlier
    rm settings-fixed.xml

# Stage 3: the final image, using a distroless image which contains just java and jvm - no shell, package manager etc
# So this is smaller, more secure, etc than using say alpine-coretto here like we did in Stage 1 and 2.
FROM gcr.io/distroless/java17-debian12
WORKDIR /app

# Add metadata - these can be inspected later in say the pod details
# Following Open Container Initiative's label format https://github.com/opencontainers/image-spec
LABEL org.opencontainers.image.authors="GPFD team (laa-payments-finance@digital.justice.gov.uk)" \
      org.opencontainers.image.description="Pay for Legal Aid Application Tests" \
      org.opencontainers.image.vendor="Ministry of Justice" \
      org.opencontainers.image.title="Get Payments & Finance Data" \
      org.opencontainers.image.url="https://github.com/ministryofjustice/payforlegalaid-tests" \
      org.opencontainers.image.documentation="https://github.com/ministryofjustice/payforlegalaid-tests/readme.md" \
      org.opencontainers.image.source="https://github.com/ministryofjustice/payforlegalaid-tests" \
      org.opencontainers.image.licenses="MIT" \
      org.opencontainers.image.base.name="gcr.io/distroless/java17-debian11"

# Copy the jar we built in Stage 2 to our current image.
# the 65532 is the UID for the nonroot user on the distroless image. so this chown ensures the image can run the jar as needed
COPY --from=builder --chown=65532:65532 /build/target/payforlegalaid-tests-*.jar app.jar

# Tells it to set the default user to the nonroot user
USER 65532:65532
# This lets the container be run as an executable - if you start the container it will run this command
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "app.jar"]