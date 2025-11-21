plugins {
    id("java-library")
    id("maven-publish")
    id("signing")
    alias(libs.plugins.nmcp)
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(17)
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    // Main
    api(project(":problem4j-spring-web"))

    compileOnly(platform(libs.spring.boot.dependencies))
    compileOnly(libs.spring.boot.autoconfigure)
    compileOnly(libs.spring.web)
    compileOnly(libs.spring.webmvc)

    compileOnly(libs.jakarta.validation.api)
    compileOnly(libs.jakarta.servlet.api)
    compileOnly(libs.slf4j.api)

    annotationProcessor(libs.spring.boot.configuration.processor)

    // Test
    testImplementation(platform(libs.spring.boot.dependencies))
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.web)
    testImplementation(libs.spring.boot.starter.validation)

    testRuntimeOnly(libs.junit.platform.launcher)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
            from(components["java"])

            pom {
                name = "Problem4J Spring WebMVC"
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
        useInMemoryPgpKeys(System.getenv("SIGNING_KEY"), System.getenv("SIGNING_PASSWORD"))
        sign(publishing.publications["maven"])
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-parameters")
}

tasks.withType<Jar>().configureEach {
    manifest {
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Build-Jdk-Spec" to java.toolchain.languageVersion.get().toString(),
            "Created-By" to "Gradle ${gradle.gradleVersion}",
        )
    }
}

// Disable doclint to avoid errors and warnings on missing JavaDoc comments.
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
