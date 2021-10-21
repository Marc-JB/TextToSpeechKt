@file:Suppress("UNUSED_VARIABLE")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.konan.properties.Properties
import org.jetbrains.kotlin.util.capitalizeDecapitalize.toUpperCaseAsciiOnly

plugins {
    kotlin("multiplatform") version "1.5.31"
    id("com.android.library")
    `maven-publish`
    id("org.jetbrains.dokka") version "1.5.31"
}

fun getLocalProperties(): Properties {
    return Properties().also { properties ->
        try {
            file("./local.properties").inputStream().use {
                properties.load(it)
            }
        } catch (ignored: java.io.FileNotFoundException) {}
    }
}

data class Version(
    val major: Int,
    val minor: Int,
    val patch: Int = 0,
    val branch: String? = null
) {
    val code = major * 100 + minor * 10 + patch

    val name = "$major.$minor.$patch" + (if(!branch.isNullOrBlank()) "-$branch" else "")

    override fun toString() = name
}

val libVersion = Version(0, 7, 1, "alpha")

group = "nl.marc.tts"
version = libVersion.name

fun addPom(publication: MavenPublication) {
    publication.groupId = "nl.marc.tts"

    publication.artifactId = "tts-" + when {
        publication.artifactId.endsWith("-android") -> "android"
        publication.artifactId.endsWith("-browser") -> "browser"
        publication.artifactId.endsWith("-metadata") -> "metadata"
        else -> "common"
    }
}

tasks {
    withType(KotlinCompile::class.java).configureEach {
        kotlinOptions {
            useIR = true
            jvmTarget = JavaVersion.VERSION_1_6.toString()
        }
    }

    val printVersion by creating {
        doLast {
            println(libVersion.name)
        }
    }
}

kotlin {
    android {
        publishLibraryVariantsGroupedByFlavor = true
        publishAllLibraryVariants()
    }

    js("browser", IR) {
        browser()

        binaries.executable()
    }

    targets.forEach {
        it.mavenPublication {
            addPom(this)
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(kotlin("stdlib-common"))
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val androidMain by getting {
            dependencies {
                api(kotlin("stdlib"))
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }

        val browserMain by getting {
            dependencies {
                api(kotlin("stdlib-js"))
            }
        }
        val browserTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

android {
    compileSdk = 31
    buildToolsVersion = "31.0.0"

    sourceSets {
        getByName("main") {
            manifest.srcFile("./src/androidMain/AndroidManifest.xml")
        }
    }

    defaultConfig {
        minSdk = 19
        targetSdk = 31

        // versionCode = libVersion.code
        // versionName = libVersion.name

        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_6
        targetCompatibility = JavaVersion.VERSION_1_6
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
        forEach {
            if(it is MavenPublication) {
                addPom(it)
            }
        }
    }
}
