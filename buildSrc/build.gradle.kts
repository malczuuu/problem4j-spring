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
