plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library.kmp) apply false
    alias(libs.plugins.android.lint) apply false

    alias(libs.plugins.jetbrains.compose) apply false
    alias(libs.plugins.jetbrains.compose.compiler) apply false

    alias(libs.plugins.jetbrains.dokka)

    alias(libs.plugins.kotlin.multiplatform) apply false

    alias(libs.plugins.mavenPublishing) apply false

    alias(libs.plugins.versioncheck)
}

dependencies {
    dokkaPlugin(libs.jetbrains.dokka.plugins.androidDocumentation)
    dokkaPlugin(libs.jetbrains.dokka.plugins.versioning)
    dokka(projects.tts)
    dokka(projects.ttsCompose)
}

val libraryVersion = rootProject.findProperty("nl.marc_apps.tts.version")?.toString() ?: "0.0.1"

val currentVersion = libraryVersion.substring(0, libraryVersion.indexOf('.', libraryVersion.indexOf('.') + 1))
val dokkaWorkingDir = rootProject.layout.buildDirectory.asFile.get().resolve("dokka")
val versionArchiveDirectory = dokkaWorkingDir.resolve("html_version_archive")
val currentVersionDir = versionArchiveDirectory.resolve(currentVersion)

val dokkaWebPublishDirectory = rootProject.layout.projectDirectory.dir("out")

tasks {
    val dokkaCopyDocsToOutputDir = register<Copy>("dokkaCopyDocsToOutputDir") {
        from(currentVersionDir)
        into(dokkaWebPublishDirectory)
    }

    val dokkaDeleteOlderVersions = register<Delete>("dokkaDeleteOlderVersions") {
        delete(currentVersionDir.resolve("older"))
    }

    dokkaDeleteOlderVersions {
        mustRunAfter(dokkaCopyDocsToOutputDir)
    }

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