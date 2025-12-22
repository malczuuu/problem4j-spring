import internal.findSnapshotVersion

group = property("internal.group") as String

// In order to avoid hardcoding snapshot versions, version is derived from the current Git commit hash.
// For CI/CD add -Pversion={releaseVersion} parameter to match Git tag.
if (version == Project.DEFAULT_VERSION) {
    version = findSnapshotVersion()
}

repositories {
    mavenCentral()
}

// Utility to clean up old jars as they can clutter due to versioning by Git commit hashes.
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
