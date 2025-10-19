import com.vanniktech.maven.publish.KotlinMultiplatform
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.kotlin.multiplatform)

    alias(libs.plugins.mavenPublishing)
}

val libraryVersion = rootProject.findProperty("nl.marc_apps.tts.version")?.toString() ?: "0.0.1"

group = "nl.marc-apps"
version = libraryVersion

kotlin {
    abiValidation {
        @OptIn(ExperimentalAbiValidation::class)
        enabled.set(true)
    }

    listOf(
        js(),
        @OptIn(ExperimentalWasmDsl::class)
        wasmJs()
    ).forEach { webTarget ->
        webTarget.browser()
        webTarget.binaries.executable()
    }

    sourceSets {
        webMain.dependencies {
            implementation(libs.kotlinx.browser)
        }
    }
}

mavenPublishing {
    coordinates("nl.marc-apps", "tts-browser-definitions", libraryVersion)
    configure(KotlinMultiplatform())
    publishToMavenCentral()
    signAllPublications()
}