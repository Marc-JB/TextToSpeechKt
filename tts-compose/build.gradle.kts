@file:Suppress("UnstableApiUsage")

import org.jetbrains.dokka.gradle.DokkaTaskPartial
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.konan.properties.Properties
import org.jetbrains.kotlin.util.capitalizeDecapitalize.toUpperCaseAsciiOnly
import java.net.URL

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    `maven-publish`
    signing
    id("org.jetbrains.compose")
    alias(libs.plugins.dokka)
}

val useWasmTarget = "wasm" in libs.versions.tts.get()

class ProjectInfo {
    val groupId = "nl.marc-apps"

    val id = "tts-compose"

    val name = "TextToSpeechKt"

    val developer = Developer()

    class Developer {
        val orgName = "Marc Apps & Software"

        val website = "https://marc-apps.nl"

        val name = "Marc"

        val email = "16156117+Marc-JB@users.noreply.github.com"

        val githubName = "Marc-JB"
    }

    val repoLocation = "github.com/${developer.githubName}/TextToSpeechKt"

    val repoLocationHttp = "https://$repoLocation"

    val documentationUrl = "https://marc-jb.github.io/TextToSpeechKt"
}

val projectInfo = ProjectInfo()

val config by lazy { Config() }

group = projectInfo.groupId
version = libs.versions.tts.get()

kotlin {
    js("browserJs", IR) {
        browser()
        binaries.executable()
    }

    if (useWasmTarget) {
        @OptIn(ExperimentalWasmDsl::class)
        wasmJs("browserWasm") {
            browser()
            binaries.executable()
        }
    }

    androidTarget {
        publishLibraryVariantsGroupedByFlavor = true
        publishAllLibraryVariants()
    }

    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                if (useWasmTarget) {
                    implementation(libs.kotlin.coroutines.wasm)
                } else {
                    implementation(libs.kotlin.coroutines)
                }
                api(project(":tts"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(compose.foundation)
            }
        }
        val browserJsMain by getting {}
        if (useWasmTarget) {
            val browserWasmMain by getting {}
            val browserMain by creating {
                dependsOn(commonMain)
                browserJsMain.dependsOn(this)
                browserWasmMain.dependsOn(this)
            }
        } else {
            val browserMain by creating {
                dependsOn(commonMain)
                browserJsMain.dependsOn(this)
            }
        }
    }
}

android {
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    buildToolsVersion = libs.versions.android.buildTools.get()

    namespace = "nl.marc_apps.tts_compose"

    defaultConfig {
        minSdk = 21

        setProperty("archivesBaseName", projectInfo.id)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.kotlin.compiler.extensions.get()
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
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
            remoteUrl.set(URL("${projectInfo.repoLocationHttp}/blob/main/${projectInfo.id}/src/${name}/kotlin"))
            remoteLineSuffix.set("#L")
        }

        externalDocumentationLink {
            url.set(URL(projectInfo.documentationUrl))
            packageListUrl.set(URL("${projectInfo.documentationUrl}/package-list"))
        }

        jdkVersion.set(JavaVersion.VERSION_1_8.majorVersion.toInt())
    }
}

val javadocJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
    dependsOn(tasks.dokkaHtmlPartial)
    archiveClassifier.set("javadoc")
    from(buildDir.toPath().resolve("dokka"))
}

publishing {
    repositories {
        maven {
            name = "OSSRH"
            url = uri(
                if("SNAPSHOT" in libs.versions.tts.get()) {
                    "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                } else {
                    "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                }
            )

            credentials {
                username = config["ossrh", "username"]
                password = config["ossrh", "password"]
            }
        }

        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Marc-JB/TextToSpeechKt")
            credentials {
                username = config["gpr", "user"]
                password = config["gpr", "key"]
            }
        }
    }

    publications {
        withType<MavenPublication> {
            configurePublication()
        }
    }
}

signing {
    isRequired = true

    val signingKey = config["gpg", "signing", "key"]
    val signingPassword = config["gpg", "signing", "password"]
    useInMemoryPgpKeys(signingKey, signingPassword)

    sign(publishing.publications)
}

fun MavenPublication.configurePublication() {
    groupId = projectInfo.groupId

    artifactId = projectInfo.id + when {
        artifactId.endsWith("-android", ignoreCase = true) || name == "android" -> "-android"
        artifactId.endsWith("-browser", ignoreCase = true) -> "-browser"
        artifactId.endsWith("-browserJs", ignoreCase = true) -> "-browser-js"
        artifactId.endsWith("-browserWasm", ignoreCase = true) -> "-browser-wasm"
        artifactId.endsWith("-desktop", ignoreCase = true) -> "-desktop"
        else -> ""
    }

    artifact(javadocJar.get())

    pom {
        name.set(projectInfo.name)
        description.set(
            "Kotlin Multiplatform Text-to-Speech library for Android and browser (Kotlin/JS & Kotlin/Wasm). " +
                    "This library will enable you to use Text-to-Speech in multiplatform Kotlin projects."
        )
        url.set(projectInfo.repoLocationHttp)
        inceptionYear.set("2020")

        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/licenses/MIT")
            }
        }

        organization {
            name.set(projectInfo.developer.orgName)
            url.set(projectInfo.developer.website)
        }

        developers {
            developer {
                id.set(projectInfo.developer.githubName)
                name.set(projectInfo.developer.name)
                email.set(projectInfo.developer.email)
                url.set(projectInfo.developer.website)
                organization.set(projectInfo.developer.orgName)
                organizationUrl.set(projectInfo.developer.website)
            }
        }

        issueManagement {
            url.set("${projectInfo.repoLocationHttp}/issues")
        }

        ciManagement {
            url.set("${projectInfo.repoLocationHttp}/actions")
        }

        scm {
            connection.set("scm:git:git://${projectInfo.repoLocation}.git")
            developerConnection.set("scm:git:ssh://${projectInfo.repoLocation}.git")
            url.set(projectInfo.repoLocationHttp)
        }
    }
}

class Config {
    private val localProperties by lazy {
        Properties().also { properties ->
            try {
                project.rootProject.file("local.properties").inputStream().use {
                    properties.load(it)
                }
            } catch (ignored: java.io.FileNotFoundException) {}
        }
    }

    operator fun get(vararg path: String): String? {
        return findProperty(path.joinToString("."))?.toString()
            ?: localProperties.getProperty(path.joinToString("."))
            ?: System.getenv(path.joinToString("_") { it.toUpperCaseAsciiOnly() })
    }
}

// TODO: Remove when this is fixed.
afterEvaluate {
    val projects = mutableListOf("KotlinMultiplatform", "Android", "BrowserJs", "Desktop")

    if (useWasmTarget) {
        projects += "BrowserWasm"
    }

    val repositories = listOf("OSSRH", "GitHubPackages")
    for (currentProject in projects) {
        for (repository in repositories) {
            for (otherProject in projects) {
                if (currentProject != otherProject) {
                    tasks.named("publish${currentProject}PublicationTo${repository}Repository").configure {
                        mustRunAfter("sign${otherProject}Publication")
                    }
                }
            }
        }
    }
}
