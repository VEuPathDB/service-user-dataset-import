import org.veupathdb.lib.gradle.container.util.Logger.Level

plugins {
    java
    id("org.veupathdb.lib.gradle.container.container-utils") version "3.4.3"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

// configure VEupathDB container plugin
containerBuild {

    // Change if debugging the build process is necessary.
    logLevel = Level.Trace

    // General project level configuration.
    project {

        // Project Name
        name = "user-dataset-import"

        // Project Group
        group = "org.veupathdb.service"

        // Project Version
        version = "3.0.0"

        // Project Root Package
        projectPackage = "org.veupathdb.service.userds"

        // Main Class Name
        mainClassName = "Main"
    }

    // Docker build configuration.
    docker {

        // Docker build context
        context = "."

        // Name of the target docker file
        dockerFile = "Dockerfile"

        // Resulting image tag
        imageName = "user-dataset-import"

    }

    generateJaxRS {
        // List of custom arguments to use in the jax-rs code generation command
        // execution.
        arguments = listOf(/*arg1, arg2, arg3*/)

        // Map of custom environment variables to set for the jax-rs code generation
        // command execution.
        environment = mapOf(/*Pair("env-key", "env-val"), Pair("env-key", "env-val")*/)
    }

}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.shadowJar {
    exclude("**/Log4j2Plugins.dat")
    archiveFileName.set("service.jar")
}

repositories {
  mavenLocal()
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

//
// Project Dependencies
//

// versions
val coreLib       = "6.7.4"         // Container core lib version
val edaCommon     = "9.1.0"         // EDA Common version
val fgputil       = "2.7.1-jakarta" // FgpUtil version

val jersey        = "3.0.4"       // Jersey/JaxRS version
val jackson       = "2.13.3"      // FasterXML Jackson version
val junit         = "5.8.2"       // JUnit version
val log4j         = "2.17.2"      // Log4J version
val metrics       = "0.15.0"      // Prometheus lib version

// ensures changing modules are never cached
configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
}

dependencies {

    // Core lib, prefers local checkout if available
    implementation(findProject(":core") ?: "org.veupathdb.lib:jaxrs-container-core:${coreLib}")

    // published VEuPathDB libs
    implementation("org.gusdb:fgputil-db:${fgputil}")

    // Postgres
    implementation("org.postgresql:postgresql:42.3.3")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("io.vulpine.lib:sql-import:0.2.1")

    // iRODS
    implementation("org.irods.jargon:jargon-core:4.3.1.0-RELEASE")

    // Jersey
    implementation("org.glassfish.jersey.core:jersey-server:${jersey}")
    implementation("org.glassfish.jersey.media:jersey-media-json-jackson:${jersey}")
    implementation("org.glassfish.jersey.media:jersey-media-multipart:${jersey}")

    // Jackson
    implementation("com.fasterxml.jackson.core:jackson-databind:${jackson}")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:${jackson}")

    // Log4J
    implementation("org.apache.logging.log4j:log4j-api:${log4j}")
    implementation("org.apache.logging.log4j:log4j-core:${log4j}")

    // Metrics
    implementation("io.prometheus:simpleclient:${metrics}")
    implementation("io.prometheus:simpleclient_common:${metrics}")

    // CLI
    implementation("info.picocli:picocli:4.6.3")
    annotationProcessor("info.picocli:picocli-codegen:4.6.3")

    // Utils
    implementation("io.vulpine.lib:Jackfish:1.1.0")
    implementation("com.devskiller.friendly-id:friendly-id:1.1.0")
    implementation("io.vulpine.lib:iffy:1.0.1")

    // Unit Testing
    testImplementation("org.junit.jupiter:junit-jupiter:${junit}")
    testImplementation("org.mockito:mockito-core:4.3.1")

}

val test by tasks.getting(Test::class) {
    // Use junit platform for unit tests
    useJUnitPlatform()
}
