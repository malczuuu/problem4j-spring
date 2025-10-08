plugins {
    id("java-platform")
    id("maven-publish")
    id("signing")
    id("com.gradleup.nmcp")
}

dependencies {
    constraints {
        api("io.github.malczuuu.problem4j:problem4j-core:${property("problem4j-core.version")}")
        api("io.github.malczuuu.problem4j:problem4j-jackson:${property("problem4j-jackson.version")}")
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
                name = project.name
                description = "BOM of Spring integration for library implementing RFC7807"
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
