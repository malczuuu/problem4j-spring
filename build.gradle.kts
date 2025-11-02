import com.diffplug.spotless.LineEnding

plugins {
    id("com.diffplug.spotless").version("8.0.0")
    id("com.gradleup.nmcp.aggregation").version("1.2.0")
    id("com.gradleup.nmcp").version("1.2.0").apply(false)
}

subprojects {
    group = "io.github.malczuuu.problem4j"

    /**
     * In order to avoid hardcoding snapshot versions, we derive the version from the current Git
     * commit hash. For CI/CD add -Pversion={releaseVersion} parameter to match Git tag.
     */
    version =
        if (version == "unspecified") {
            getSnapshotVersion(rootProject.rootDir)
        } else {
            version
        }

    /**
     * Usage:
     *   ./gradlew printVersion
     */
    tasks.register("printVersion") {
        description = "Prints the current project version to the console"
        group = "help"
        doLast {
            println("${project.name} version: ${project.version}")
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
    format("misc") {
        target("**/.gitattributes", "**/.gitignore")

        trimTrailingWhitespace()
        leadingTabsToSpaces(4)
        endWithNewline()
        lineEndings = LineEnding.UNIX
    }

    java {
        target("**/src/**/*.java")

        googleJavaFormat("1.28.0")
        forbidWildcardImports()
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

        ktlint()
        endWithNewline()
        lineEndings = LineEnding.UNIX
    }
}
