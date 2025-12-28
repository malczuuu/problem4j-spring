plugins {
    id("internal.convention-java-library")
    id("internal.convention-publishing")
    alias(libs.plugins.nmcp)
}

dependencies {
    // Main
    api(project(":problem4j-spring-web"))

    compileOnly(platform(libs.spring.boot.dependencies))
    compileOnly(libs.spring.boot.autoconfigure)
    compileOnly(libs.spring.boot.webflux)

    compileOnly(libs.jakarta.validation.api)
    compileOnly(libs.slf4j.api)

    annotationProcessor(platform(libs.spring.boot.dependencies))
    annotationProcessor(libs.spring.boot.configuration.processor)

    // Test
    testImplementation(platform(libs.spring.boot.dependencies))
    testImplementation(libs.spring.boot.starter.webflux)
    testImplementation(libs.spring.boot.starter.webflux.test)
    testImplementation(libs.spring.boot.validation)

    testRuntimeOnly(libs.junit.platform.launcher)
}

// see buildSrc/src/main/kotlin/internal.convention-publishing.gradle.kts
internalPublishing {
    displayName = "Problem4J Spring WebFlux"
    description = "Spring WebFlux integration for library implementing RFC7807 (aka RFC9457)."
}
