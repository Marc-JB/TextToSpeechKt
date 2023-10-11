@file:Suppress("UnstableApiUsage")

import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")

    id("org.jetbrains.compose")

    id("com.android.application")
}

kotlin {
    androidTarget()

    js("browser", IR) {
        browser()

        binaries.executable()
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
                implementation(compose.material)
                implementation(compose.materialIconsExtended)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation(project(":tts-compose"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(compose.preview)
                implementation(compose.uiTooling)
                implementation("androidx.navigation:navigation-compose:2.7.4")
                implementation("androidx.activity:activity-compose:1.8.0")
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.preview)
                implementation(compose.uiTooling)
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

android {
    compileSdk = 34
    buildToolsVersion = "34.0.0"

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
        targetSdk = 34

        versionCode = 1
        versionName = "1.0"

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
        kotlinCompilerExtensionVersion = "1.5.3"
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
            packageVersion = "1.0.0"
            windows {
                perUserInstall = true
            }
        }
    }
}
