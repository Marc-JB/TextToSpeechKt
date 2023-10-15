import org.jetbrains.dokka.versioning.VersioningConfiguration
import org.jetbrains.dokka.versioning.VersioningPlugin

plugins {
    val androidVersion = "8.1.0"
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

dependencies {
    dokkaPlugin("org.jetbrains.dokka:android-documentation-plugin:1.9.0")
    dokkaPlugin("org.jetbrains.dokka:versioning-plugin:1.9.0")
}

val currentVersion = "2.1"
val dokkaWorkingDir = project.rootProject.buildDir.resolve("dokka")
val versionArchiveDirectory = dokkaWorkingDir.resolve("html_version_archive")
val currentVersionDir = versionArchiveDirectory.resolve(currentVersion)

tasks.dokkaHtmlMultiModule {
    outputDirectory.set(currentVersionDir)

    pluginConfiguration<VersioningPlugin, VersioningConfiguration> {
        version = currentVersion
        olderVersionsDir = versionArchiveDirectory
    }

    doLast {
        copy {
            from(currentVersionDir)
            into(dokkaWorkingDir.resolve("html"))
        }
        currentVersionDir.resolve("older").deleteRecursively()
    }
}
