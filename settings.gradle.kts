pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

rootProject.name = "problem4j-spring"

include(":problem4j-spring-bom")
include(":problem4j-spring-web")
include(":problem4j-spring-webflux")
include(":problem4j-spring-webmvc")
