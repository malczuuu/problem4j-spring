// Note that usage of version catalogs in buildSrc is not as straightforward as in regular modules.
// For more information, see:
// https://docs.gradle.org/current/userguide/version_catalogs.html#sec:buildsrc-version-catalog
plugins {
    `kotlin-dsl`
}

version = "current"

repositories {
    mavenCentral()
}
