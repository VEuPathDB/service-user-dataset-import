# # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
#
#   Build Service & Dependencies
#
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
FROM veupathdb/alpine-dev-base:jdk-15 AS prep

LABEL service="user-dataset-import"

ARG GITHUB_USERNAME
ARG GITHUB_TOKEN

WORKDIR /workspace
RUN jlink --compress=2 --module-path /opt/jdk/jmods \
       --add-modules java.base,java.logging,java.xml,java.desktop,java.management,java.sql,java.naming,java.net.http,java.security.jgss \
       --output /jlinked \
    && apk add --no-cache git sed findutils coreutils make npm \
    && git config --global advice.detachedHead false

RUN apk update \
    && apk add ca-certificates curl  \
    && curl https://veupathdb.org/common/apidb-ca-rsa.crt -o /usr/local/share/ca-certificates/apidb-ca-rsa.crt \
    && update-ca-certificates \
    && keytool -import -storepass changeit -noprompt -file /usr/local/share/ca-certificates/apidb-ca-rsa.crt -keystore /opt/jdk/lib/security/cacerts

ENV DOCKER=build
COPY makefile gradlew ./
COPY gradle gradle

RUN make install-dev-env

COPY [ \
  "build.gradle.kts", \
  "dependencies.gradle.kts", \
  "settings.gradle.kts", \
  "service.properties", \
  "./" \
]

RUN mkdir -p vendor \
    && cp -n /jdbc/* vendor \
    && echo Installing Gradle \
    && ./gradlew dependencies --info --configuration runtimeClasspath

COPY api.raml .
COPY schema schema
COPY src src

RUN make jar

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
#
#   Run the service
#
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
FROM foxcapades/alpine-oracle:1.6

LABEL service="user-dataset-import"

RUN apk add --no-cache tzdata \
    && cp /usr/share/zoneinfo/America/New_York /etc/localtime \
    && echo "America/New_York" > /etc/timezone

ENV JAVA_HOME=/opt/jdk \
    PATH=/opt/jdk/bin:$PATH \
    JVM_MEM_ARGS="-Xms=32M -Xmx=256M" \
    JVM_ARGS=""

COPY --from=prep /jlinked /opt/jdk
COPY --from=prep /opt/jdk/lib/security/cacerts /opt/jdk/lib/security/cacerts
COPY --from=prep /workspace/build/libs/service.jar /service.jar
COPY config.json .

CMD java -jar -XX:+CrashOnOutOfMemoryError $JVM_MEM_ARGS $JVM_ARGS /service.jar
