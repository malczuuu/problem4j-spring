plugins {
    id("internal.convention-java-platform")
    id("internal.convention-publishing")
    alias(libs.plugins.nmcp)
}

dependencies {
    constraints {
        api(libs.problem4j.core)
        api(libs.problem4j.jackson)
        api(libs.problem4j.jackson3)
        api(project(":problem4j-spring-web"))
        api(project(":problem4j-spring-webflux"))
        api(project(":problem4j-spring-webmvc"))
    }
}

// see buildSrc/src/main/kotlin/internal.convention-publishing.gradle.kts
internalPublishing {
    displayName = "Problem4J Spring BOM"
    description = "BOM of Spring integration for library implementing RFC7807"
}
