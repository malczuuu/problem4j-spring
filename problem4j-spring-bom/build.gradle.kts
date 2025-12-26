plugins {
    id("java-platform")
    id("maven-publish")
    id("signing")
    alias(libs.plugins.nmcp)
}

dependencies {
    constraints {
        api(libs.problem4j.core)
        api(libs.problem4j.jackson)
        api(project(":problem4j-spring-web"))
        api(project(":problem4j-spring-webflux"))
        api(project(":problem4j-spring-webmvc"))
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
            from(components["javaPlatform"])

            pom {
                name = "[Deprecated] Problem4J Spring BOM"
                description =
                    "[Deprecated] BOM of Spring integration for library implementing RFC7807. Migrated to io.github.problem4j:problem4j-spring-bom namespace."
                url = "https://github.com/malczuuu/${rootProject.name}"
                inceptionYear = "2025"
                licenses {
                    license {
                        name = "MIT License"
                        url = "https://opensource.org/license/MIT"
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
                    url = "https://github.com/problem4j/${rootProject.name}/issues"
                }
                scm {
                    connection = "scm:git:https://github.com/problem4j/${rootProject.name}.git"
                    developerConnection = "scm:git:git@github.com:problem4j/${rootProject.name}.git"
                    url = "https://github.com/problem4j/${rootProject.name}"
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
