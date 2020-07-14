plugins {
    kotlin("multiplatform") version "1.3.72"
    id("com.android.library")
    maven
    `maven-publish`
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

val libVersion = Version(0, 1, 9)

group = "nl.marc.tts"
version = libVersion.name

fun addPom(publ: MavenPublication) {
    publ.groupId = "nl.marc.tts"

    publ.artifactId = "tts-" + when {
        publ.artifactId.endsWith("-android") -> "android"
        publ.artifactId.endsWith("-browser") -> "browser"
        publ.artifactId.endsWith("-metadata") -> "metadata"
        else -> "common"
    }
}

repositories {
    gradlePluginPortal()
    google()
    jcenter()
    mavenCentral()
}

kotlin {
    android {
        publishLibraryVariants("release")
    }

    js("browser") {
        browser()
    }

    targets.forEach {
        it.mavenPublication {
            addPom(this)
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
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
                implementation(kotlin("stdlib"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }

        val browserMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
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
    compileSdkVersion(29)
    buildToolsVersion = "30.0.1"

    defaultConfig {
        minSdkVersion(1)
        targetSdkVersion(29)
        versionCode = libVersion.code
        versionName = libVersion.name
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            isZipAlignEnabled = true
            isCrunchPngs = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_6
        targetCompatibility = JavaVersion.VERSION_1_6
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Marc-JB/TextToSpeechKt")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
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
