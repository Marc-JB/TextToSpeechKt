@file:Suppress("UnstableApiUsage")

import org.jetbrains.dokka.gradle.DokkaTaskPartial
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import java.net.URL

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    `maven-publish`
    signing
    alias(libs.plugins.dokka)
}

val projectId = "core"

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

        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget = JvmTarget.JVM_1_8
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "tts"
            isStatic = true
        }
    }

    jvm("desktop") {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
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
            implementation(libs.kotlin.coroutines)
        }

        androidMain.dependencies {
            implementation(libs.androidx.annotation)
        }

        getByName("desktopMain").dependencies {
            implementation(libs.freetts)
        }
    }
}

android {
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    buildToolsVersion = libs.versions.android.buildTools.get()

    namespace = getTtsScopedProperty("namespace")

    defaultConfig {
        minSdk = 1

        setProperty("archivesBaseName", getTtsScopedProperty("artifactId"))
    }
}

tasks.withType(KotlinCompilationTask::class) {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
}

dependencies {
    dokkaPlugin(libs.dokka.plugins.androidDocs)
    dokkaPlugin(libs.dokka.plugins.versioning)
}

tasks.withType<DokkaTaskPartial>().configureEach {
    dokkaSourceSets.configureEach {
        sourceLink {
            localDirectory.set(file("src/${name}/kotlin"))
            remoteUrl.set(URL("https://${getTtsProperty("git", "location")}/blob/main/${getTtsScopedProperty("artifactId")}/src/${name}/kotlin"))
            remoteLineSuffix.set("#L")
        }

        externalDocumentationLink {
            url.set(URL(getTtsProperty("documentation", "url")))
            packageListUrl.set(URL("${getTtsProperty("documentation", "url")}/package-list"))
        }

        if (name.startsWith("android")){
            jdkVersion.set(JavaVersion.VERSION_1_8.majorVersion.toInt())
        } else if (name.startsWith("desktop")){
            jdkVersion.set(JavaVersion.VERSION_21.majorVersion.toInt())
        }
    }
}

val javadocJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
    dependsOn(tasks.dokkaHtmlPartial)
    archiveClassifier.set("javadoc")
    from(layout.buildDirectory.asFile.get().resolve("dokka"))
}

publishing {
    repositories {
        configureOssrhRepository("SNAPSHOT" in libs.versions.tts.get(), getConfigProperty("ossrh", "username"), getConfigProperty("ossrh", "password"))

        // configureGitHubPackagesRepository("Marc-JB", "TextToSpeechKt", getConfigProperty("gpr", "user"), getConfigProperty("gpr", "key"))
    }

    publications {
        withType<MavenPublication> {
            configureMavenPublication(project, this, javadocJar, getTtsScopedProperty("artifactId")!!)
        }
    }
}

signing {
    isRequired = true

    val signingKey = getConfigProperty("gpg", "signing", "key")
    val signingPassword = getConfigProperty("gpg", "signing", "password")
    useInMemoryPgpKeys(signingKey, signingPassword)

    sign(publishing.publications)
}

afterEvaluate {
    val publicationTaskNames = tasks.names.filter { it.startsWith("publish") && "PublicationTo" in it && it.endsWith("Repository") }
    val signTaskNames = tasks.names.filter { it.startsWith("sign") && it.endsWith("Publication") }.toTypedArray()
    for (publicationTaskName in publicationTaskNames) {
        tasks.getByName(publicationTaskName) {
            dependsOn(*signTaskNames)
        }
    }
}

private fun getTtsScopedProperty(vararg path: String) = getTtsProperty(projectId, *path)
