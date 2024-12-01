@file:Suppress("UnstableApiUsage")

import org.jetbrains.dokka.gradle.DokkaTaskPartial
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.net.URI

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    `maven-publish`
    signing
    alias(libs.plugins.dokka)
    alias(libs.plugins.mavenRepositoryConfiguration)
    alias(libs.plugins.ttsPublication)
}

object Project {
    const val ARTIFACT_ID = "tts"
    const val NAMESPACE = "nl.marc_apps.tts"
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

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlin.coroutines)
        }

        androidMain.dependencies {
            implementation(libs.androidx.annotation)
        }

        jvmMain.dependencies {
            implementation(libs.freetts)
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
        minSdk = 1

        setProperty("archivesBaseName", Project.ARTIFACT_ID)
    }
}

dependencies {
    dokkaPlugin(libs.dokka.plugins.androidDocs)
    dokkaPlugin(libs.dokka.plugins.versioning)
}

tasks {
    withType<DokkaTaskPartial>().configureEach {
        dokkaSourceSets.configureEach {
            sourceLink {
                localDirectory.set(file("src/${name}/kotlin"))
                remoteUrl.set(URI.create("https://github.com/Marc-JB/TextToSpeechKt/blob/main/${Project.ARTIFACT_ID}/src/${name}/kotlin").toURL())
                remoteLineSuffix.set("#L")
            }

            externalDocumentationLink {
                url.set(URI.create("https://marc-jb.github.io/TextToSpeechKt").toURL())
                packageListUrl.set(URI.create("https://marc-jb.github.io/TextToSpeechKt/package-list").toURL())
            }

            if (name.startsWith("android")){
                jdkVersion.set(JavaVersion.VERSION_1_8.majorVersion.toInt())
            } else if (name.startsWith("jvm")){
                jdkVersion.set(JavaVersion.VERSION_17.majorVersion.toInt())
            }
        }
    }

    matching {
        it.name.startsWith("publish") && "PublicationTo" in it.name && it.name.endsWith("Repository")
    }.configureEach {
        dependsOn(matching { it.name.startsWith("sign") && it.name.endsWith("Publication") })
    }

    register<Jar>("javadocJar") {
        dependsOn(dokkaHtmlPartial)
        archiveClassifier.set("javadoc")
        from(layout.buildDirectory.asFile.get().resolve("dokka"))
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
    javadocJarTask.set(tasks.named<Jar>("javadocJar").get())
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
