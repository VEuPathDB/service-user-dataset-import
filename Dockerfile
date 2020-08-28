# # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
#
#   Build Service & Dependencies
#
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
FROM veupathdb/alpine-dev-base:latest AS prep

LABEL service="user-dataset-import"

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
FROM foxcapades/alpine-oracle:1.3

LABEL service="user-dataset-import"

ENV JAVA_HOME=/opt/jdk \
    PATH=/opt/jdk/bin:$PATH

COPY --from=prep /jlinked /opt/jdk
COPY --from=prep /opt/jdk/lib/security/cacerts /opt/jdk/lib/security/cacerts
COPY --from=prep /workspace/build/libs/service.jar /service.jar
COPY config.json .

CMD java -jar /service.jar
