@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")

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
    android {
        publishLibraryVariantsGroupedByFlavor = true
        publishAllLibraryVariants()
    }

    js("browser", IR) {
        binaries.executable()

        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled = true
                }
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
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("androidx.annotation:annotation:1.5.0")
            }
        }

        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
            }
        }

        val browserMain by getting
        val browserTest by getting

        val desktopMain by getting {
            dependencies {
                implementation("net.sf.sociaal:freetts:1.2.2")
            }
        }
        val desktopTest by getting
    }
}

android {
    compileSdk = 32
    buildToolsVersion = "33.0.0"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    
    namespace = "nl.marc_apps.tts"

    defaultConfig {
        minSdk = 1
        targetSdk = 32

        setProperty("archivesBaseName", "tts")

        buildConfigField("String", "LIBRARY_VERSION", "\"${ProjectInfo.VERSION}\"")

        testBuildType = "debug"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}
