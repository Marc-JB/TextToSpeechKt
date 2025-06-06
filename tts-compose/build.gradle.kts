@file:Suppress("UnstableApiUsage")

import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.dokka)
    alias(libs.plugins.mavenPublishPlugin)
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
    macosArm64()
    macosX64()

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

mavenPublishing {
    coordinates("nl.marc-apps", Project.ARTIFACT_ID, libs.versions.tts.get())

    configure(KotlinMultiplatform(
        javadocJar = JavadocJar.Dokka("dokkaGeneratePublicationHtml")
    ))

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    /*repositories {
        maven {
            name = "githubPackages"
            url = uri("https://maven.pkg.github.com/Marc-JB/TextToSpeechKt")
            credentials(PasswordCredentials::class)
        }
    }*/

    signAllPublications()
}
