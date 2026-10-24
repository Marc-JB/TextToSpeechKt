import com.sun.net.httpserver.HttpServer
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.resources.ResourcesExtension
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library.kmp)
    alias(libs.plugins.android.lint)

    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.jetbrains.compose.compiler)

    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(21)
        vendor = JvmVendorSpec.JETBRAINS
    }

    android {
        compileSdk = 37
        minSdk = 21

        namespace = "nl.marc_apps.tts.demo.shared"

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
        macosArm64(),
    ).forEach { appleTarget ->
        appleTarget.binaries.framework {
            baseName = "Shared"
        }
    }

    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
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
        all {
            languageSettings {
                optIn("nl.marc_apps.tts.experimental.ExperimentalVoiceApi")
                optIn("nl.marc_apps.tts.experimental.ExperimentalDesktopTarget")
            }
        }

        commonMain.dependencies {
            implementation(libs.jetbrains.compose.runtime)
            implementation(libs.jetbrains.compose.foundation)
            implementation(libs.jetbrains.compose.ui)
            implementation(libs.jetbrains.compose.material3)
            implementation(libs.jetbrains.compose.material.icons.extended)
            implementation(libs.jetbrains.compose.components.resources)
            implementation(libs.jetbrains.compose.uiToolingPreview)
            implementation(libs.jetbrains.androidx.lifecycle.viewmodelCompose)
            implementation(libs.jetbrains.androidx.lifecycle.runtimeCompose)

            implementation(projects.ttsCompose)
        }

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.jetbrains.compose.uiTooling)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.jetbrains.compose.uiTooling)
        }

        webMain.dependencies {
            implementation(libs.kotlinx.browser)
        }
    }
}

compose.resources {
    publicResClass = false
    packageOfResClass = "nl.marc_apps.tts.demo.resources"
    generateResClass = ResourcesExtension.ResourceClassGeneration.Always
}

compose.desktop {
    application {
        mainClass = "nl.marc_apps.tts.demo.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "TTS Demo"
            packageVersion = "1.0.0"
        }
    }
}

