import org.jetbrains.dokka.versioning.VersioningPlugin
import org.jetbrains.dokka.versioning.VersioningConfiguration

plugins {
    val androidVersion = "8.0.2"
    val kotlinVersion = "1.9.10"

    id("com.android.application") version androidVersion apply false
    id("com.android.library") version androidVersion apply false

    kotlin("multiplatform") version kotlinVersion apply false
    id("org.jetbrains.dokka") version "1.9.0"

    id("org.jetbrains.compose") version "1.5.2" apply false
}

buildscript {
    dependencies {
        classpath("org.jetbrains.dokka:versioning-plugin:1.9.0")
    }
}

val currentVersion = "2.0"
val previousVersionsDirectory = project.rootProject.buildDir.resolve("dokka").resolve("html_version_archive").invariantSeparatorsPath

tasks.dokkaHtmlMultiModule {
    pluginConfiguration<VersioningPlugin, VersioningConfiguration> {
        version = currentVersion
        olderVersionsDir = file(previousVersionsDirectory)
    }
}
