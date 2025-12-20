import com.diffplug.spotless.LineEnding

plugins {
    alias(libs.plugins.nmcp).apply(false)
    alias(libs.plugins.nmcp.aggregation)
    alias(libs.plugins.spotless)
}

subprojects {
    group = "io.github.malczuuu.problem4j"

    // In order to avoid hardcoding snapshot versions, version is derived from the current Git commit hash. For CI/CD
    // add -Pversion={releaseVersion} parameter to match Git tag.
    if (version == Project.DEFAULT_VERSION) {
        version = getSnapshotVersion(rootProject.rootDir)
    }

    // Utility to clean up old jars as they can clutter due to versioning by Git commit hashes.
    // Usage:
    //   ./gradlew cleanLibs
    tasks.register("cleanLibs") {
        description = "Deletes build/libs directory."
        group = "build"
        delete(layout.buildDirectory.dir("libs"))
    }

    // Usage:
    //   ./gradlew printVersion
    tasks.register<DefaultTask>("printVersion") {
        description = "Prints the current project version to the console."
        group = "help"

        val projectName = project.name
        val projectVersion = project.version.toString()

        doLast {
            println("$projectName version: $projectVersion")
        }
    }
}

allprojects {
    repositories {
        mavenCentral()
    }
}

dependencies {
    nmcpAggregation(project(":problem4j-spring-bom"))
    nmcpAggregation(project(":problem4j-spring-web"))
    nmcpAggregation(project(":problem4j-spring-webflux"))
    nmcpAggregation(project(":problem4j-spring-webmvc"))
}

nmcpAggregation {
    centralPortal {
        username = System.getenv("PUBLISHING_USERNAME")
        password = System.getenv("PUBLISHING_PASSWORD")

        publishingType = "USER_MANAGED"
    }
}

spotless {
    java {
        target("**/src/**/*.java")

        // NOTE: decided not to upgrade Google Java Format, as versions 1.29+ require running it on Java 21
        googleJavaFormat("1.28.0")
        forbidWildcardImports()
        endWithNewline()
        lineEndings = LineEnding.UNIX
    }

    kotlin {
        target("**/src/**/*.kt")

        ktfmt("0.59").metaStyle()
        endWithNewline()
        lineEndings = LineEnding.UNIX
    }

    kotlinGradle {
        target("**/*.gradle.kts")

        ktlint("1.8.0").editorConfigOverride(mapOf("max_line_length" to "120"))
        endWithNewline()
        lineEndings = LineEnding.UNIX
    }

    format("yaml") {
        target("**/*.yml", "**/*.yaml")

        trimTrailingWhitespace()
        leadingTabsToSpaces(2)
        endWithNewline()
        lineEndings = LineEnding.UNIX
    }

    format("misc") {
        target("**/.gitattributes", "**/.gitignore")

        trimTrailingWhitespace()
        leadingTabsToSpaces(4)
        endWithNewline()
        lineEndings = LineEnding.UNIX
    }
}
