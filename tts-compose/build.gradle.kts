import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.android.library.kmp)
    alias(libs.plugins.android.lint)

    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.jetbrains.compose.compiler)

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

    jvmToolchain(8)

    androidLibrary {
        compileSdk = 36
        minSdk = 21

        namespace = "nl.marc_apps.tts_compose"

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
            jvmTarget.set(JvmTarget.JVM_11)
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
            api(projects.tts)

            implementation(compose.runtime)
        }

        androidMain.dependencies {
            implementation(compose.foundation)
        }
    }
}

dokka {
    dokkaSourceSets.configureEach {
        sourceLink {
            localDirectory = file("src/$name/kotlin")
            remoteUrl("https://github.com/Marc-JB/TextToSpeechKt/blob/main/tts-compose/src/$name/kotlin")
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
    coordinates("nl.marc-apps", "tts-compose", libraryVersion)

    configure(KotlinMultiplatform(
        javadocJar = JavadocJar.Dokka("dokkaGeneratePublicationHtml")
    ))

    publishToMavenCentral()

    signAllPublications()
}
