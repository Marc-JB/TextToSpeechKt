import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.android.library.kmp)
    alias(libs.plugins.android.lint)

    alias(libs.plugins.jetbrains.dokka)

    alias(libs.plugins.kotlin.multiplatform)

    alias(libs.plugins.mavenPublishing)
}

val libraryVersion = rootProject.findProperty("nl.marc_apps.tts.version")?.toString() ?: "0.0.1"

group = "nl.marc-apps"
version = libraryVersion

kotlin {
    abiValidation {
        @OptIn(ExperimentalAbiValidation::class)
        enabled.set(true)
    }

    jvmToolchain(17)

    androidLibrary {
        compileSdk = 36
        minSdk = 1

        namespace = "nl.marc_apps.tts"

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosArm64()
    macosX64()

    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }

    listOf(
        js(),
        @OptIn(ExperimentalWasmDsl::class)
        wasmJs()
    ).forEach { webTarget ->
        webTarget.browser()
        webTarget.binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines)
            implementation(libs.androidx.annotation)
        }

        jvmMain.dependencies {
            implementation(libs.freetts)
        }

        webMain.dependencies {
            implementation(projects.ttsBrowserDefinitions)

            implementation(libs.kotlinx.browser)
        }
    }
}

dokka {
    dokkaSourceSets.configureEach {
        sourceLink {
            localDirectory = file("src/$name/kotlin")
            remoteUrl("https://github.com/Marc-JB/TextToSpeechKt/blob/main/tts/src/$name/kotlin")
            remoteLineSuffix = "#L"
        }

        externalDocumentationLinks {
            create("tts") {
                url("https://marc-jb.github.io/TextToSpeechKt")
                packageListUrl("https://marc-jb.github.io/TextToSpeechKt/package-list")
            }
        }
    }
}

mavenPublishing {
    coordinates("nl.marc-apps", "tts", libraryVersion)

    configure(KotlinMultiplatform(
        javadocJar = JavadocJar.Dokka("dokkaGeneratePublicationHtml")
    ))

    publishToMavenCentral()

    signAllPublications()
}
