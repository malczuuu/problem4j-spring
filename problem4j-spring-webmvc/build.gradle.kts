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

repositories {
    mavenCentral()
}

dependencies {
    // Main
    api(project(":problem4j-spring-web"))

    api("org.springframework:spring-webmvc:${property("spring.version")}")

    compileOnly("jakarta.validation:jakarta.validation-api:${property("jakarta.validation-api.version")}")
    compileOnly("jakarta.servlet:jakarta.servlet-api:${property("jakarta.servlet-api.version")}")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:${property("spring-boot.version")}")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test:${property("spring-boot.version")}")
    testImplementation("org.springframework.boot:spring-boot-starter-validation:${property("spring-boot.version")}")
    testImplementation("org.springframework.boot:spring-boot-starter-webflux:${property("spring-boot.version")}")

    testImplementation("jakarta.servlet:jakarta.servlet-api:${property("jakarta.servlet-api.version")}")

    testImplementation(platform("org.junit:junit-bom:${property("junit-bom.version")}"))
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
                    connection = "scm:git:git://github.com/malczuuu/${rootProject.name}.git"
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
}
