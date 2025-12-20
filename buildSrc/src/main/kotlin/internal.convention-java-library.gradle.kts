import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    id("internal.convention-common")
    id("java-library")
}

java {
    toolchain.languageVersion = providers.gradleProperty("internal.java.version").map { JavaLanguageVersion.of(it) }
    withSourcesJar()
    withJavadocJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-parameters")
}

tasks.withType<Jar>().configureEach {
    dependsOn("cleanLibs")
    manifest {
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Build-Jdk-Spec" to java.toolchain.languageVersion.get().toString(),
            "Created-By" to "Gradle ${gradle.gradleVersion}",
        )
    }
    from("../LICENSE") {
        into("META-INF/")
        rename { "LICENSE.txt" }
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

    testLogging {
        events("passed", "skipped", "failed", "standardOut", "standardError")
        exceptionFormat = TestExceptionFormat.SHORT
        showStandardStreams = true
    }

    // For resolving warnings from mockito.
    jvmArgs("-XX:+EnableDynamicAgentLoading")

    systemProperty("user.language", "en")
    systemProperty("user.country", "US")
}
