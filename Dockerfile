# # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
#
#   Build Service & Dependencies
#
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
FROM veupathdb/alpine-dev-base:jdk-17 AS prep

LABEL service="user-dataset-import"

WORKDIR /workspace

RUN jlink --compress=2 --module-path /opt/jdk/jmods \
       --add-modules java.base,java.logging,java.xml,java.desktop,java.management,java.sql,java.naming,java.net.http,java.security.jgss,jdk.crypto.ec \
       --output /jlinked \
    && apk add --no-cache git sed findutils coreutils make npm \
    && git config --global advice.detachedHead false

RUN apk update \
    && apk add ca-certificates curl \
    && curl https://veupathdb.org/common/apidb-ca-rsa.crt -o /usr/local/share/ca-certificates/apidb-ca-rsa.crt \
    && update-ca-certificates \
    && mkdir -p /opt/jdk/lib/security \
    && keytool -import -storepass changeit -noprompt -file /usr/local/share/ca-certificates/apidb-ca-rsa.crt -keystore /opt/jdk/lib/security/cacerts

ARG GITHUB_USERNAME
ARG GITHUB_TOKEN

ENV DOCKER=build

# copy files required to build dev environment and fetch dependencies
COPY makefile build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle gradle

# cache build environment
RUN make install-dev-env

# cache gradle and dependencies installation
RUN ./gradlew dependencies

# copy remaining files
COPY . .

# build the project
RUN make jar

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
#
#   Run the service
#
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
FROM alpine:3.16

LABEL service="user-dataset-import"

RUN apk add --no-cache tzdata \
    && cp /usr/share/zoneinfo/America/New_York /etc/localtime \
    && echo "America/New_York" > /etc/timezone

ENV JAVA_HOME=/opt/jdk \
    PATH=/opt/jdk/bin:$PATH \
    JVM_MEM_ARGS="-Xms=32M -Xmx=256M" \
    JVM_ARGS=""

COPY --from=prep /jlinked /opt/jdk
COPY --from=prep /usr/lib/jvm/default-jvm/lib/security/cacerts /opt/jdk/lib/security/cacerts
COPY --from=prep /workspace/build/libs/service.jar /service.jar
COPY config.json .

CMD java -jar -XX:+CrashOnOutOfMemoryError $JVM_MEM_ARGS $JVM_ARGS /service.jar
