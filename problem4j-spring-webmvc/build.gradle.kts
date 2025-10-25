plugins {
    id("java-library")
    id("maven-publish")
    id("signing")
    id("com.gradleup.nmcp")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(17)
    withSourcesJar()
    withJavadocJar()
}

val springBootVersion: String by project

dependencies {
    // Main
    api(project(":problem4j-spring-web"))

    compileOnly(platform("org.springframework.boot:spring-boot-dependencies:${springBootVersion}"))
    compileOnly("org.springframework.boot:spring-boot-autoconfigure")
    compileOnly("org.springframework:spring-web")
    compileOnly("org.springframework:spring-webmvc")

    compileOnly("jakarta.validation:jakarta.validation-api")
    compileOnly("jakarta.servlet:jakarta.servlet-api")
    compileOnly("org.slf4j:slf4j-api")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:${springBootVersion}")

    // Test
    testImplementation(platform("org.springframework.boot:spring-boot-dependencies:${springBootVersion}"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-validation")

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
                description = "Spring WebMVC integration for library implementing RFC7807"
                url = "https://github.com/malczuuu/${rootProject.name}"
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
                    url = "https://github.com/malczuuu/${rootProject.name}/issues"
                }
                scm {
                    connection = "scm:git:https://github.com/malczuuu/${rootProject.name}.git"
                    developerConnection = "scm:git:git@github.com:malczuuu/${rootProject.name}.git"
                    url = "https://github.com/malczuuu/${rootProject.name}"
                }
            }
        }
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
    systemProperty("user.language", "en")
    systemProperty("user.country", "US")
}
