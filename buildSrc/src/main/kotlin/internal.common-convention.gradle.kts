group = property("internal.group") as String

//
// Not assigning version as it is assigned in CI/CD using -Pversion=X.Y.Z parameter.
//

repositories {
    mavenCentral()
}

// Utility to clean up old jars as they can clutter.
// Usage:
//   ./gradlew cleanLibs
tasks.register<Delete>("cleanLibs") {
    description = "Deletes build/libs directory."
    group = "build"

    delete(layout.buildDirectory.dir("libs"))
}

// Usage:
//   ./gradlew printVersion
tasks.register<DefaultTask>("printVersion") {
    description = "Prints the current project version to the console."
    group = "help"

    val projectName = project.name
    val projectVersion = project.version.toString()

    doLast {
        println("$projectName version: $projectVersion")
    }
}
