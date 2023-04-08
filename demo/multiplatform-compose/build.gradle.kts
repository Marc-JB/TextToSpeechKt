@file:Suppress("UnstableApiUsage")

import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")

    id("org.jetbrains.compose")

    id("com.android.application")
}

kotlin {
    android()

    js("browser", IR) {
        binaries.executable()

        browser {
            commonWebpackConfig {
                cssSupport {}
            }
        }
    }

    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
        }

        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                implementation(project(":tts-compose"))
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val androidMain by getting {
            dependencies {
                api("androidx.compose.runtime:runtime:1.3.1")
                implementation("androidx.compose.ui:ui:1.3.1")
                implementation("androidx.compose.foundation:foundation:1.3.1")
                implementation("androidx.compose.foundation:foundation-layout:1.3.1")
                implementation("androidx.compose.ui:ui-tooling-preview:1.3.1")
                implementation("androidx.navigation:navigation-compose:2.5.3")
                implementation("androidx.activity:activity-compose:1.7.0")
                implementation("androidx.compose.material:material:1.3.1")
                implementation("androidx.compose.material:material-icons-extended:1.3.1")
            }
        }

        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
            }
        }

        val browserMain by getting {
            dependencies {
                implementation(compose.web.core)
            }
        }
        val browserTest by getting

        val desktopMain by getting {
            dependencies {
                implementation(compose.preview)
                implementation(compose.uiTooling)
                implementation(compose.materialIconsExtended)
                implementation(compose.desktop.currentOs)
            }
        }
        val desktopTest by getting
    }
}

dependencies {
    debugImplementation("androidx.compose.ui:ui-tooling:1.3.1")
}

android {
    compileSdk = 33
    buildToolsVersion = "33.0.2"

    sourceSets {
        named("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            res.srcDirs("src/androidMain/resources", "src/commonMain/resources")
        }
    }

    namespace = "nl.marc_apps.tts_demo"

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
        targetSdk = 33

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.1"
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
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
