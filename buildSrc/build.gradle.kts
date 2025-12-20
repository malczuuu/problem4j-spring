// Note that usage of version catalogs in buildSrc is not as straightforward as in regular modules.
// For more information, see:
// https://docs.gradle.org/current/userguide/version_catalogs.html#sec:buildsrc-version-catalog
plugins {
    `kotlin-dsl`
}

version = "current"

// Kotlin does not yet support 25 JDK target, to be revisited in the future.
if (JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_25)) {
    kotlin {
        jvmToolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }
}

repositories {
    mavenCentral()
}
