
import org.jetbrains.dokka.versioning.VersioningConfiguration
import org.jetbrains.dokka.versioning.VersioningPlugin

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false

    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.dokka)

    alias(libs.plugins.compose) apply false
    alias(libs.plugins.compose.compiler) apply false

    alias(libs.plugins.versioncheck)
}

buildscript {
    dependencies {
        classpath(libs.dokka.plugins.versioning)
    }
}

dependencies {
    dokkaPlugin(libs.dokka.plugins.androidDocs)
    dokkaPlugin(libs.dokka.plugins.versioning)
}

val rawVersion = libs.versions.tts.get()
val currentVersion = rawVersion.substring(0, rawVersion.indexOf('.', rawVersion.indexOf('.') + 1))
val dokkaWorkingDir = project.rootProject.layout.buildDirectory.asFile.get().resolve("dokka")
val versionArchiveDirectory = dokkaWorkingDir.resolve("html_version_archive")
val currentVersionDir = versionArchiveDirectory.resolve(currentVersion)

tasks {
    val dokkaCopyDocsToOutputDir by register<Copy>("dokkaCopyDocsToOutputDir") {
        from(currentVersionDir)
        into(dokkaWorkingDir.resolve("html"))
    }

    val dokkaDeleteOlderVersions by register<Delete>("dokkaDeleteOlderVersions") {
        delete(currentVersionDir.resolve("older"))
    }

    dokkaDeleteOlderVersions.mustRunAfter(dokkaCopyDocsToOutputDir)

    dokkaHtmlMultiModule {
        outputDirectory.set(currentVersionDir)

        pluginConfiguration<VersioningPlugin, VersioningConfiguration> {
            version = currentVersion
            olderVersionsDir = versionArchiveDirectory
        }

        finalizedBy(dokkaCopyDocsToOutputDir, dokkaDeleteOlderVersions)
    }

    dependencyUpdates {
        rejectVersionIf {
            arrayOf("alpha", "beta", "rc").any { it in candidate.version.lowercase() }
        }
    }
}
