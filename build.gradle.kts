import org.veupathdb.lib.gradle.container.util.Logger.Level

plugins {
    java
    id("org.veupathdb.lib.gradle.container.container-utils") version "4.8.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

// configure VEupathDB container plugin
containerBuild {

    // Change if debugging the build process is necessary.
    logLevel = Level.Info

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

// versions of VEuPathDB libs
val coreLib       = "7.0.0"         // Container core lib version
val edaCommon     = "11.7.2"          // EDA Common version
val fgputil       = "2.13.1-jakarta"  // FgpUtil version

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
    implementation("org.postgresql:postgresql:42.5.0")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("io.vulpine.lib:sql-import:0.2.1")

    // iRODS
    implementation("org.irods.jargon:jargon-core:4.3.1.0-RELEASE")

    // Jersey
    implementation("org.glassfish.jersey.core:jersey-server:3.1.1")
    implementation("org.glassfish.jersey.media:jersey-media-json-jackson:3.1.1")
    implementation("org.glassfish.jersey.media:jersey-media-multipart:3.1.1")

    // Jackson
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.3")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.15.3")

    // Log4J
    implementation("org.apache.logging.log4j:log4j-api:2.20.0")
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")

    // Metrics
    implementation("io.prometheus:simpleclient:0.16.0")
    implementation("io.prometheus:simpleclient_common:0.16.0")

    // CLI
    implementation("info.picocli:picocli:4.7.3")
    annotationProcessor("info.picocli:picocli-codegen:4.7.3")

    // Utils
    implementation("io.vulpine.lib:Jackfish:1.1.0")
    implementation("com.devskiller.friendly-id:friendly-id:1.1.0")
    implementation("io.vulpine.lib:iffy:1.0.1")

    // Unit Testing
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testImplementation("org.mockito:mockito-core:5.2.0")

}

val test by tasks.getting(Test::class) {
    // Use junit platform for unit tests
    useJUnitPlatform()
}
