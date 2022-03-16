import java.util.Properties
import java.io.FileInputStream
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
  java
  id("org.veupathdb.lib.gradle.container.container-utils") version "3.2.0"
  id("com.github.johnrengelman.shadow") version "7.1.2"
}

// Load Props
val buildProps = Properties()
buildProps.load(FileInputStream(File(rootDir, "service.properties")))
val fullPack = "${buildProps["app.package.root"]}.${buildProps["app.package.service"]}"
val genPack = fullPack

java {
  targetCompatibility = JavaVersion.VERSION_15
  sourceCompatibility = JavaVersion.VERSION_15
}

// Project settings
group = buildProps["project.group"] ?: error("empty 1")
version = buildProps["project.version"] ?: error("empty 2")

containerBuild {
  fgputil {
    version = "4f2eb70"
  }

  project {
    projectPackage = "org.veupathdb.service.userds"
  }
}

repositories {
  mavenCentral()
  maven {
    name = "GitHubPackages"
    url = uri("https://maven.pkg.github.com/veupathdb/maven-packages")
    credentials {
      username = project.findProperty("gpr.user") as String?
        ?: System.getenv("GITHUB_USERNAME")
      password = project.findProperty("gpr.key") as String?
        ?: System.getenv("GITHUB_TOKEN")
    }
  }
  maven {
    url =
      uri("https://raw.githubusercontent.com/DICE-UNC/DICE-Maven/master/releases")
  }
}


val metrics = "0.9.0"  // Prometheus lib version

dependencies {

  //
  // FgpUtil & Compatibility Dependencies
  //

  // FgpUtil jars
  implementation(files(
    "vendor/fgputil-accountdb-1.0.0.jar",
    "vendor/fgputil-core-1.0.0.jar",
    "vendor/fgputil-db-1.0.0.jar",
    "vendor/fgputil-web-1.0.0.jar"
  ))

  // Compatibility bridge to support the long dead log4j-1.X
  runtimeOnly("org.apache.logging.log4j:log4j-1.2-api:2.17.1")

  // Extra FgpUtil dependencies
  runtimeOnly("org.apache.commons:commons-dbcp2:2.9.0")
  runtimeOnly("org.json:json:20211205")
  runtimeOnly("com.fasterxml.jackson.datatype:jackson-datatype-json-org:2.13.2")
  runtimeOnly("com.fasterxml.jackson.module:jackson-module-parameter-names:2.13.2")
  runtimeOnly("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.13.2")
  runtimeOnly("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.2")

  //
  // Project Dependencies
  //

  // Oracle
  runtimeOnly(files(
    "vendor/ojdbc8.jar",
    "vendor/ucp.jar",
    "vendor/xstreams.jar"
  ))

  // Postgres
  implementation("org.postgresql:postgresql:42.3.3")
  implementation("com.zaxxer:HikariCP:5.0.1")
  implementation("io.vulpine.lib:sql-import:0.2.1")

  // iRODS
  implementation("org.irods.jargon:jargon-core:4.3.1.0-RELEASE")

  // Core lib, prefers local checkout if available
  implementation(findProject(":core")
    ?: "org.veupathdb.lib:jaxrs-container-core:5.6.1")

  // Jersey
  implementation("org.glassfish.jersey.containers:jersey-container-grizzly2-http:2.33")
  implementation("org.glassfish.jersey.containers:jersey-container-grizzly2-servlet:2.33")
  implementation("org.glassfish.jersey.media:jersey-media-json-jackson:2.33")
  implementation("org.glassfish.jersey.media:jersey-media-multipart:2.33")
  runtimeOnly("org.glassfish.jersey.inject:jersey-hk2:2.33")

  // Jackson
  implementation("com.fasterxml.jackson.core:jackson-databind:2.13.2")
  implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.2")

  // Log4J
  implementation("org.apache.logging.log4j:log4j-api:2.17.1")
  implementation("org.apache.logging.log4j:log4j-core:2.17.1")
  implementation("org.apache.logging.log4j:log4j:2.16.0")

  // Metrics
  implementation("io.prometheus:simpleclient:0.15.0")
  implementation("io.prometheus:simpleclient_common:0.15.0")

  // CLI
  implementation("info.picocli:picocli:4.6.3")
  annotationProcessor("info.picocli:picocli-codegen:4.6.3")

  // Utils
  implementation("io.vulpine.lib:Jackfish:1.1.0")
  implementation("io.vulpine.lib:iffy:1.0.1")
  implementation("com.devskiller.friendly-id:friendly-id:1.1.0")

  // Unit Testing
  testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
  testImplementation("org.mockito:mockito-core:4.3.1")
}

tasks.shadowJar {
  archiveBaseName.set("service")
  archiveClassifier.set("")
  archiveVersion.set("")

  exclude("**/Log4j2Plugins.dat")
}

tasks.register("print-gen-package") { print(genPack) }
tasks.register("print-container-name") { print(buildProps["container.name"]) }

tasks.withType<Test> {
  testLogging {
    events.addAll(listOf(TestLogEvent.FAILED,
      TestLogEvent.SKIPPED,
      TestLogEvent.STANDARD_OUT,
      TestLogEvent.STANDARD_ERROR,
      TestLogEvent.PASSED))

    exceptionFormat = TestExceptionFormat.FULL
    showExceptions = true
    showCauses = true
    showStackTraces = true
    showStandardStreams = true
    enableAssertions = true
  }
  ignoreFailures = true // Always try to run all tests for all modules
}

val test by tasks.getting(Test::class) {
  // Use junit platform for unit tests
  useJUnitPlatform()
}
