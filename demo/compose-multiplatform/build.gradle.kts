@file:Suppress("UnstableApiUsage")

import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    id("org.jetbrains.compose")
}

val useWasmTarget = "wasm" in libs.versions.tts.get()

kotlin {
    androidTarget()

    js("browserJs", IR) {
        moduleName = "compose-multiplatform"
        browser()
        binaries.executable()
    }

    if (useWasmTarget) {
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
    }

    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                if (useWasmTarget) {
                    implementation(libs.kotlin.coroutines.wasm)
                } else {
                    implementation(libs.kotlin.coroutines)
                }
                implementation(project(":tts-compose"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(compose.preview)
                implementation(compose.uiTooling)
                implementation("androidx.navigation:navigation-compose:2.7.5")
                implementation("androidx.activity:activity-compose:1.8.1")
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.preview)
                implementation(compose.uiTooling)
                implementation(compose.desktop.currentOs)
            }
        }

        val browserJsMain by getting {}
        if (useWasmTarget) {
            val browserWasmMain by getting {}
            val browserMain by creating {
                dependsOn(commonMain)
                browserJsMain.dependsOn(this)
                browserWasmMain.dependsOn(this)
            }
        } else {
            val browserMain by creating {
                dependsOn(commonMain)
                browserJsMain.dependsOn(this)
            }
        }
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
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

    packagingOptions {
        resources {
            excludes += "kotlin/**"
            excludes += "**/*.kotlin_metadata"
            excludes += "DebugProbesKt.bin"
            excludes += "META-INF/*.kotlin_module"
            // excludes += "META-INF/*.version"
            excludes += "META-INF/AL2.0"
            excludes += "META-INF/LGPL2.1"
            excludes += "build-data.properties"
            excludes += "play-**.properties"
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
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }

        getByName("debug") {
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.kotlin.compiler.extensions.get()
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

compose.experimental {
    web.application {}
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
