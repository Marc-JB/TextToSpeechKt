@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    `maven-publish`
    signing
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.dokka)
    alias(libs.plugins.mavenRepositoryConfiguration)
    alias(libs.plugins.ttsPublication)
}

object Project {
    const val ARTIFACT_ID = "tts-compose"
    const val NAMESPACE = "nl.marc_apps.tts_compose"
}

group = "nl.marc-apps"
version = libs.versions.tts.get()

kotlin {
    js {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    androidTarget {
        publishLibraryVariants("release")

        compilerOptions {
            jvmTarget = JvmTarget.JVM_1_8
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    jvm {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    applyDefaultHierarchyTemplate {
        common {
            group("webCommonW3C") {
                withJs()
                withWasmJs()
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(libs.kotlin.coroutines)
            api(projects.tts)
        }

        androidMain.dependencies {
            implementation(compose.foundation)
        }

        wasmJsMain.dependencies {
            implementation(libs.kotlin.browser)
        }
    }
}

android {
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    buildToolsVersion = libs.versions.android.buildTools.get()

    namespace = Project.NAMESPACE

    defaultConfig {
        minSdk = 21

        setProperty("archivesBaseName", Project.ARTIFACT_ID)
    }
}

tasks {
    matching {
        it.name.startsWith("publish") && "PublicationTo" in it.name && it.name.endsWith("Repository")
    }.configureEach {
        dependsOn(matching { it.name.startsWith("sign") && it.name.endsWith("Publication") })
    }
}

val dokkaHtmlJar by tasks.registering(Jar::class) {
    description = "A HTML Documentation JAR containing Dokka HTML"
    from(tasks.dokkaGeneratePublicationHtml.flatMap { it.outputDirectory })
    archiveClassifier = "html-doc"
}

dokka {
    dokkaSourceSets.configureEach {
        sourceLink {
            localDirectory = file("src/${name}/kotlin")
            remoteUrl("https://github.com/Marc-JB/TextToSpeechKt/blob/main/${Project.ARTIFACT_ID}/src/${name}/kotlin")
            remoteLineSuffix = "#L"
        }

        externalDocumentationLinks {
            create("tts") {
                url("https://marc-jb.github.io/TextToSpeechKt")
                packageListUrl("https://marc-jb.github.io/TextToSpeechKt/package-list")
            }
        }

        if (name.startsWith("android")){
            jdkVersion.set(JavaVersion.VERSION_1_8.majorVersion.toInt())
        } else if (name.startsWith("jvm")){
            jdkVersion.set(JavaVersion.VERSION_17.majorVersion.toInt())
        }
    }
}

publishingRepositories {
    isSnapshot = "SNAPSHOT" in libs.versions.tts.get()

    ossrh {
        username = getConfigProperty("ossrh", "username")
        token = getConfigProperty("ossrh", "password")
    }

    /*githubPackages {
        organization = "Marc-JB"
        project = "TextToSpeechKt"
        username = getConfigProperty("gpr", "user")
        token = getConfigProperty("gpr", "key")
    }*/
}

configureTtsPublication {
    javadocJarTask.set(dokkaHtmlJar)
}

signing {
    isRequired = true

    val signingKey = getConfigProperty("gpg", "signing", "key")
    val signingPassword = getConfigProperty("gpg", "signing", "password")
    useInMemoryPgpKeys(signingKey, signingPassword)

    sign(publishing.publications.matching { it is MavenPublication })
}

fun getConfigProperty(vararg path: String): String? {
    return findProperty(path.joinToString(".")) as? String
        ?: System.getenv(path.joinToString("_") { it.uppercase() })
}
