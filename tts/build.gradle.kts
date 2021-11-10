@file:Suppress("UNUSED_VARIABLE")

import org.jetbrains.kotlin.konan.properties.Properties
import org.jetbrains.kotlin.util.capitalizeDecapitalize.toUpperCaseAsciiOnly

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    `maven-publish`
    signing
    id("org.jetbrains.dokka")
}

object ProjectInfo {
    const val GROUP_ID = "nl.marc-apps"

    const val NAME = "TextToSpeechKt"

    const val VERSION = "0.8.0"

    object Developer {
        const val ORG_NAME = "Marc Apps & Software"

        const val WEBSITE = "https://marc-apps.nl"

        const val NAME = "Marc"

        const val EMAIL = "16156117+Marc-JB@users.noreply.github.com"

        const val GITHUB_NAME = "Marc-JB"
    }

    const val LOCATION = "github.com/${Developer.GITHUB_NAME}/TextToSpeechKt"

    const val LOCATION_HTTP = "https://$LOCATION"
}

group = ProjectInfo.GROUP_ID
version = ProjectInfo.VERSION

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
        val androidMain by getting {
            dependencies {
                implementation("androidx.annotation:annotation:1.3.0")
            }
        }
    }
}

android {
    compileSdk = 31
    buildToolsVersion = "31.0.0"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        minSdk = 1
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

val javadocJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

fun configurePublication(publication: MavenPublication) {
    publication.groupId = ProjectInfo.GROUP_ID

    publication.artifactId = "tts" + when {
        publication.artifactId.endsWith("-android") || publication.name == "android" -> "-android"
        publication.artifactId.endsWith("-browser") -> "-browser"
        else -> ""
    }

    publication.artifact(javadocJar.get())

    publication.pom {

        name.set(ProjectInfo.NAME)
        description.set(
            "Multiplatform Text-to-Speech library for Android and Browser (JS). " +
                    "This library will enable you to use Text-to-Speech in multiplatform Kotlin projects."
        )
        url.set(ProjectInfo.LOCATION_HTTP)
        inceptionYear.set("2020")

        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/licenses/MIT")
            }
        }

        organization {
            name.set(ProjectInfo.Developer.ORG_NAME)
            url.set(ProjectInfo.Developer.WEBSITE)
        }

        developers {
            developer {
                id.set(ProjectInfo.Developer.GITHUB_NAME)
                name.set(ProjectInfo.Developer.NAME)
                email.set(ProjectInfo.Developer.EMAIL)
                url.set(ProjectInfo.Developer.WEBSITE)
                organization.set(ProjectInfo.Developer.ORG_NAME)
                organizationUrl.set(ProjectInfo.Developer.WEBSITE)
            }
        }

        issueManagement {
            url.set("${ProjectInfo.LOCATION_HTTP}/issues")
        }

        ciManagement {
            url.set("${ProjectInfo.LOCATION_HTTP}/actions")
        }

        scm {
            connection.set("scm:git:git://${ProjectInfo.LOCATION}.git")
            developerConnection.set("scm:git:ssh://${ProjectInfo.LOCATION}.git")
            url.set(ProjectInfo.LOCATION_HTTP)
        }
    }
}

val keys = getLocalProperties()

fun getProperty(key: String): String? {
    return keys.getProperty(key) ?: System.getenv(key.toUpperCaseAsciiOnly().replace(".", "_"))
}

publishing {
    repositories {
        maven {
            name = "OSSRH"
            url = uri(
                if(project.version.toString().endsWith("-SNAPSHOT")) {
                    "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                } else {
                    "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                }
            )

            credentials {
                username = getProperty("ossrh.username")
                password = getProperty("ossrh.password")
            }
        }

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

signing {
    isRequired = true

    val signingKey = getProperty("gpg.signing.key")
    val signingPassword = getProperty("gpg.signing.password")
    useInMemoryPgpKeys(signingKey, signingPassword)

    sign(publishing.publications)
}
