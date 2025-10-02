import com.diffplug.spotless.LineEnding

plugins {
    id("java-library")
    id("maven-publish")
    id("signing")
    id("com.diffplug.spotless").version("8.0.0")
    id("com.gradleup.nmcp").version("1.2.0")
}

group = "io.github.malczuuu.problem4j"

/**
 * In order to avoid hardcoding snapshot versions, we derive the version from the current Git commit hash. For CI/CD add
 * -Pversion={releaseVersion} parameter to match Git tag.
 */
version =
    if (version == "unspecified")
        getSnapshotVersion(rootProject.rootDir)
    else
        version

java {
    toolchain.languageVersion = JavaLanguageVersion.of(17)
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
}

val jakartaServletApiVersion = "6.0.0"
val jakartaValidationApiVersion = "3.0.2"

val problem4jCoreVersion = "1.1.0"
val problem4jJacksonVersion = "1.0.0"

val springBootVersion = "3.5.6"
val springVersion = "6.2.11"

val junitVersion = "5.13.4"

dependencies {
    // Main
    api("org.springframework.boot:spring-boot-autoconfigure:${springBootVersion}")
    api("org.springframework:spring-webmvc:${springVersion}")

    api("io.github.malczuuu.problem4j:problem4j-core:${problem4jCoreVersion}")
    api("io.github.malczuuu.problem4j:problem4j-jackson:${problem4jJacksonVersion}")

    compileOnly("jakarta.servlet:jakarta.servlet-api:${jakartaServletApiVersion}")
    compileOnly("jakarta.validation:jakarta.validation-api:${jakartaValidationApiVersion}")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:${springBootVersion}")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test:${springBootVersion}")
    testImplementation("org.springframework.boot:spring-boot-starter-validation:${springBootVersion}")

    testImplementation("jakarta.servlet:jakarta.servlet-api:${jakartaServletApiVersion}")

    testImplementation(platform("org.junit:junit-bom:${junitVersion}"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
            from(components["java"])

            pom {
                name = project.name
                description = "Spring Web MVC integration for library implementing RFC7807"
                url = "https://github.com/malczuuu/${project.name}"
                inceptionYear = "2025"
                licenses {
                    license {
                        name = "MIT License"
                        url = "https://opensource.org/licenses/MIT"
                    }
                }
                developers {
                    developer {
                        id = "malczuuu"
                        name = "Damian Malczewski"
                        url = "https://github.com/malczuuu"
                    }
                }
                issueManagement {
                    system = "GitHub Issues"
                    url = "https://github.com/malczuuu/${project.name}/issues"
                }
                scm {
                    connection = "scm:git:git://github.com/malczuuu/${project.name}.git"
                    developerConnection = "scm:git:git@github.com:malczuuu/${project.name}.git"
                    url = "https://github.com/malczuuu/${project.name}"
                }
            }
        }
    }
}

nmcp {
    publishAllPublicationsToCentralPortal {
        username = System.getenv("PUBLISHING_USERNAME")
        password = System.getenv("PUBLISHING_PASSWORD")

        publishingType = "USER_MANAGED"
    }
}

signing {
    if (project.hasProperty("sign")) {
        useInMemoryPgpKeys(
            System.getenv("SIGNING_KEY"),
            System.getenv("SIGNING_PASSWORD")
        )
        sign(publishing.publications["maven"])
    }
}

spotless {
    format("misc") {
        target("**/*.gradle.kts", "**/.gitattributes", "**/.gitignore")

        trimTrailingWhitespace()
        leadingTabsToSpaces(4)
        endWithNewline()
        lineEndings = LineEnding.UNIX
    }

    java {
        target("src/**/*.java")

        googleJavaFormat("1.28.0")
        forbidWildcardImports()
        lineEndings = LineEnding.UNIX
    }
}

tasks.register("printVersion") {
    doLast {
        println("Project version: $version")
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-parameters")
}

/**
 * Disable doclint to avoid errors and warnings on missing JavaDoc comments.
 */
tasks.withType<Javadoc>().configureEach {
    (options as StandardJavadocDocletOptions).apply {
        addStringOption("Xdoclint:none", "-quiet")
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
