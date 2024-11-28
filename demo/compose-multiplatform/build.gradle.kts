@file:Suppress("UnstableApiUsage")

import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.resources.ResourcesExtension.ResourceClassGeneration
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_1_8
        }

        dependencies {
            debugImplementation(compose.uiTooling)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
        }
    }

    js("browserJs") {
        moduleName = "compose-multiplatform"
        browser {
            commonWebpackConfig {
                devServer = devServer ?: KotlinWebpackConfig.DevServer()
            }
        }
        binaries.executable()
        useEsModules()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs("browserWasm") {
        moduleName = "compose-multiplatform"
        browser {
            commonWebpackConfig {
                devServer = devServer ?: KotlinWebpackConfig.DevServer()
            }
        }
        binaries.executable()
    }

    jvm("desktop") {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    applyDefaultHierarchyTemplate {
        common {
            group("browser") {
                withJs()
                withWasmJs()
            }
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        all {
            languageSettings {
                optIn("nl.marc_apps.tts.experimental.ExperimentalVoiceApi")
                optIn("nl.marc_apps.tts.experimental.ExperimentalDesktopTarget")
                optIn("nl.marc_apps.tts.experimental.ExperimentalIOSTarget")
            }
        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            // implementation(compose.components.uiToolingPreview)
            implementation(compose.components.resources)
            implementation(libs.kotlin.coroutines)
            implementation(project(":tts-compose"))
        }

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(compose.uiTooling)
            implementation("androidx.navigation:navigation-compose:2.7.7")
            implementation("androidx.activity:activity-compose:1.9.0")
        }

        named("desktopMain").dependencies {
            implementation(compose.preview)
            implementation(compose.uiTooling)
            implementation(compose.desktop.currentOs)
        }

        named("browserWasmMain").dependencies {
            implementation(libs.kotlin.browser)
        }
    }
}

android {
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    buildToolsVersion = libs.versions.android.buildTools.get()

    namespace = "nl.marc_apps.tts_demo"

    sourceSets {
        named("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            res.srcDirs("src/androidMain/resources", "src/commonMain/resources")
        }
    }

    packaging {
        jniLibs {
            excludes += setOf("kotlin/**")
        }

        resources {
            excludes += setOf(
                "kotlin/**",
                "**/*.kotlin_metadata",
                "META-INF/*.kotlin_module",
                // "META-INF/*.version",
                "META-INF/AL2.0",
                "META-INF/LGPL2.1",
                "DebugProbesKt.bin",
                "build-data.properties",
                "play-**.properties",
                "kotlin-tooling-metadata.json"
            )
        }
    }

    defaultConfig {
        applicationId = "nl.marc_apps.tts_demo"

        minSdk = 21
        targetSdk = libs.versions.android.compileSdk.get().toInt()

        versionCode = 1
        versionName = libs.versions.tts.get()

        testBuildType = "debug"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }

        named("debug") {
            applicationIdSuffix = ".debug"

            isMinifyEnabled = false
            isShrinkResources = false

            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    bundle {
        abi.enableSplit = true
        language.enableSplit = true
        density.enableSplit = true
        texture.enableSplit = true
        deviceTier.enableSplit = true
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

compose.resources {
    publicResClass = false
    packageOfResClass = "nl.marc_apps.tts_demo.resources"
    generateResClass = ResourceClassGeneration.Always
}

compose.desktop {
    application {
        buildTypes {
            release {
                proguard {
                    isEnabled.set(true)
                    obfuscate.set(true)
                }
            }
        }
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "TTS Demo"
            packageVersion = libs.versions.tts.get().substringBefore("-")
            windows {
                perUserInstall = true
            }
        }
    }
}
