plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false

    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.android) apply false

    alias(libs.plugins.ksp) apply false

    alias(libs.plugins.dokka)

    alias(libs.plugins.compose) apply false
    alias(libs.plugins.compose.compiler) apply false

    alias(libs.plugins.versioncheck)
}

dependencies {
    dokkaPlugin(libs.dokka.plugins.androidDocs)
    dokkaPlugin(libs.dokka.plugins.versioning)
    dokka(projects.tts)
    dokka(projects.ttsCompose)
}

val rawVersion = libs.versions.tts.get()
val currentVersion = rawVersion.substring(0, rawVersion.indexOf('.', rawVersion.indexOf('.') + 1))
val dokkaWorkingDir = rootProject.layout.buildDirectory.asFile.get().resolve("dokka")
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

    dokkaGenerate {
        finalizedBy(dokkaCopyDocsToOutputDir, dokkaDeleteOlderVersions)
    }

    dependencyUpdates {
        rejectVersionIf {
            arrayOf("alpha", "beta", "rc").any { it in candidate.version.lowercase() }
        }
    }
}

dokka {
    dokkaPublications.html {
        outputDirectory = currentVersionDir
    }

    pluginsConfiguration {
        versioning {
            version = currentVersion
            olderVersionsDir = versionArchiveDirectory
        }
    }
}