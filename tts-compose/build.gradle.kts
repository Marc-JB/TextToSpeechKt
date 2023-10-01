@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")

    id("org.jetbrains.compose")

    id("com.android.library")
}

object ProjectInfo {
    const val GROUP_ID = "nl.marc-apps"

    const val VERSION_MAJOR_MINOR = "2.0"

    const val VERSION_BUILD = "0"

    const val SNAPSHOT = true

    private val SNAPSHOT_SUFFIX = if (SNAPSHOT) "-SNAPSHOT" else ""

    val VERSION = "${VERSION_MAJOR_MINOR}.${VERSION_BUILD}${SNAPSHOT_SUFFIX}"
}

group = ProjectInfo.GROUP_ID
version = ProjectInfo.VERSION

kotlin {
    js("browser", IR) {
        browser()

        binaries.executable()
    }

    androidTarget {
        publishLibraryVariantsGroupedByFlavor = true
        publishAllLibraryVariants()
    }

    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
        }

        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                api(project(":tts"))
            }
        }

        val androidMain by getting {
            dependencies {
                api("androidx.compose.runtime:runtime:1.5.0")
                implementation("androidx.compose.foundation:foundation:1.5.0")
            }
        }

        val browserMain by getting {
            dependencies {
                api(compose.runtime)
            }
        }

        val desktopMain by getting {
            dependencies {
                api(compose.runtime)
            }
        }
    }
}

android {
    compileSdk = 34
    buildToolsVersion = "34.0.0"

    namespace = "nl.marc_apps.tts_compose"

    defaultConfig {
        minSdk = 21
        targetSdk = 34

        setProperty("archivesBaseName", "tts-compose")
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
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}
