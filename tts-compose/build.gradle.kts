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
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.dokka)
    alias(libs.plugins.mavenRepositoryConfiguration)
    alias(libs.plugins.ttsPublication)
}

val projectId = "compose"

group = getTtsProperty("groupId")!!
version = libs.versions.tts.get()

kotlin {
    js("browserJs", IR) {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs("browserWasm") {
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

    jvm("desktop") {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    applyDefaultHierarchyTemplate {
        common {
            group("browser") {
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

        named("browserWasmMain").dependencies {
            implementation(libs.kotlin.browser)
        }
    }
}

android {
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    buildToolsVersion = libs.versions.android.buildTools.get()

    namespace = getTtsScopedProperty("namespace")

    defaultConfig {
        minSdk = 21

        setProperty("archivesBaseName", getTtsScopedProperty("artifactId"))
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
                remoteUrl.set(URI.create("https://${getTtsProperty("git", "location")}/blob/main/${getTtsScopedProperty("artifactId")}/src/${name}/kotlin").toURL())
                remoteLineSuffix.set("#L")
            }

            externalDocumentationLink {
                url.set(URI.create(getTtsProperty("documentation", "url")).toURL())
                packageListUrl.set(URI.create("${getTtsProperty("documentation", "url")}/package-list").toURL())
            }

            if (name.startsWith("android")){
                jdkVersion.set(JavaVersion.VERSION_1_8.majorVersion.toInt())
            } else if (name.startsWith("desktop")){
                jdkVersion.set(JavaVersion.VERSION_21.majorVersion.toInt())
            }
        }
    }

    matching {
        it.name.startsWith("publish") && "PublicationTo" in it.name && it.name.endsWith("Repository")
    }.configureEach {
        dependsOn(matching { it.name.startsWith("sign") && it.name.endsWith("Publication") })
    }

    val javadocJar by register<Jar>("javadocJar") {
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

    sign(publishing.publications)
}

private fun getTtsScopedProperty(vararg path: String) = getTtsProperty(projectId, *path)
