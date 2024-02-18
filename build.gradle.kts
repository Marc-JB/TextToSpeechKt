import org.jetbrains.dokka.versioning.VersioningConfiguration
import org.jetbrains.dokka.versioning.VersioningPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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

    alias(libs.plugins.versioncheck)
}

buildscript {
    dependencies {
        classpath(libs.dokka.plugins.versioning)
    }
}

allprojects {
    afterEvaluate {
        tasks.withType<PublishToMavenRepository> {
            dependsOn(*tasks.names.filter { it.startsWith("sign") && it.endsWith("Publication") }.toTypedArray())
        }

        tasks.withType<KotlinCompile> {
            compilerOptions {
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }
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

tasks.dependencyUpdates {
    rejectVersionIf {
        arrayOf("alpha", "beta", "rc").any { it in candidate.version.lowercase() } || (("wasm" in candidate.version) xor ("wasm" in currentVersion))
    }
}
