@file:Suppress("UNUSED_VARIABLE")

import org.jetbrains.kotlin.konan.properties.Properties
import org.jetbrains.kotlin.util.capitalizeDecapitalize.toUpperCaseAsciiOnly

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    `maven-publish`
    id("org.jetbrains.dokka") version "1.5.31"
}

group = "nl.marc-apps"
version = "0.1.0"

fun getLocalProperties(): Properties {
    return Properties().also { properties ->
        try {
            file("../local.properties").inputStream().use {
                properties.load(it)
            }
        } catch (ignored: java.io.FileNotFoundException) {}
    }
}

kotlin {
    js("browser", IR) {
        browser()

        binaries.executable()
    }

    android {
        publishLibraryVariantsGroupedByFlavor = true
        publishAllLibraryVariants()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
            }
        }
        val browserMain by getting
        val androidMain by getting
    }
}

android {
    compileSdk = 31
    buildToolsVersion = "30.0.3"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        minSdk = 19
        targetSdk = 31

        setProperty("archivesBaseName", "tts")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

fun configurePublication(publication: MavenPublication) {
    publication.groupId = "nl.marc-apps"

    publication.artifactId = "tts" + when {
        publication.artifactId.endsWith("-android") || publication.name == "android" -> "-android"
        publication.artifactId.endsWith("-browser") -> "-browser"
        else -> ""
    }
}

publishing {
    val keys = getLocalProperties()

    fun getProperty(key: String): String? {
        return keys.getProperty(key) ?: System.getenv(key.toUpperCaseAsciiOnly().replace(".", "_"))
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Marc-JB/TextToSpeechKt")
            credentials {
                username = getProperty("gpr.user")
                password = getProperty("gpr.key")
            }
        }
    }

    publications {
        withType<MavenPublication> {
            configurePublication(this)
        }
    }
}
