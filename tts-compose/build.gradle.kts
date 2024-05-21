@file:Suppress("UnstableApiUsage")

import org.jetbrains.dokka.gradle.DokkaTaskPartial
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URL

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    `maven-publish`
    signing
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.dokka)
}

val projectId = "compose"
val jvmVersion = JavaVersion.VERSION_1_8

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
        publishLibraryVariantsGroupedByFlavor = true
        publishAllLibraryVariants()
    }

    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = jvmVersion.toString()
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
            api(project(":tts"))
        }

        androidMain.dependencies {
            implementation(compose.foundation)
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

    compileOptions {
        sourceCompatibility = jvmVersion
        targetCompatibility = jvmVersion
    }

    buildFeatures {
        compose = true
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = jvmVersion.toString()
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

        jdkVersion.set(jvmVersion.majorVersion.toInt())
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

        configureGitHubPackagesRepository("Marc-JB", "TextToSpeechKt", getConfigProperty("gpr", "user"), getConfigProperty("gpr", "key"))
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
