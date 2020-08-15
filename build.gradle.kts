@file:Suppress("UNUSED_VARIABLE")

import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform") version "1.3.72"
    id("com.android.library")
    maven
    `maven-publish`
    id("org.jetbrains.dokka") version "1.4.0-rc"
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

val libVersion = Version(0, 5, 0)

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

repositories {
    gradlePluginPortal()
    google()
    jcenter()
    mavenCentral()
}

tasks.withType(KotlinCompile::class.java) {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_6.toString()
    }
}

kotlin {
    android {
        publishLibraryVariantsGroupedByFlavor = true
        publishAllLibraryVariants()
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
                api(kotlin("stdlib-common"))
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
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7")
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
    compileSdkVersion(29)

    sourceSets {
        val main by getting {
            manifest.srcFile("./src/androidMain/AndroidManifest.xml")
        }
    }

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

tasks.dokkaGfm {
    outputDirectory = "$buildDir/dokka/gfm"
}

tasks.dokkaHtml {
    outputDirectory = "$buildDir/dokka/html"
}

tasks.dokkaJavadoc {
    outputDirectory = "$buildDir/dokka/javadoc"
}

tasks.dokkaJekyll {
    outputDirectory = "$buildDir/dokka/jekyll"
}

tasks.withType<DokkaTask>().configureEach {
    dokkaSourceSets {
        val commonMain by registering {
            sourceRoot {
                path = "src/commonMain/kotlin"
            }
        }

        val browserMain by registering {
            dependsOn(commonMain)
            sourceRoot {
                path = "src/browserMain/kotlin"
            }
        }

        val androidMain by registering {
            dependsOn(commonMain)
            sourceRoot {
                path = "src/androidMain/kotlin"
            }
        }
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
