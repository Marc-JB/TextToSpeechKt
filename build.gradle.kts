import org.jetbrains.dokka.versioning.VersioningConfiguration
import org.jetbrains.dokka.versioning.VersioningPlugin

plugins {
    val useWasmTarget = "wasm" in libs.versions.tts.get()

    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false

    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.dokka)

    if (useWasmTarget) {
        alias(libs.plugins.compose.wasm) apply false
    } else {
        alias(libs.plugins.compose) apply false
    }
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
