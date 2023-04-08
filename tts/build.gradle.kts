@file:Suppress("UNUSED_VARIABLE")

import org.jetbrains.dokka.gradle.GradleDokkaSourceSetBuilder
import org.jetbrains.kotlin.konan.properties.Properties
import org.jetbrains.kotlin.util.capitalizeDecapitalize.toUpperCaseAsciiOnly
import java.net.URL

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

    const val VERSION_MAJOR_MINOR = "1.3"

    const val VERSION_BUILD = "0"

    const val SNAPSHOT = false

    private val SNAPSHOT_SUFFIX = if (SNAPSHOT) "-SNAPSHOT" else ""

    val VERSION = "${VERSION_MAJOR_MINOR}.${VERSION_BUILD}${SNAPSHOT_SUFFIX}"

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
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
            }
        }
        val browserMain by getting {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.6.4")
            }
        }
        val androidMain by getting {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
                implementation("androidx.annotation:annotation:1.6.0")
            }
        }
    }
}

android {
    compileSdk = 33
    buildToolsVersion = "33.0.2"

    defaultConfig {
        minSdk = 1

        setProperty("archivesBaseName", "tts")

        buildConfigField("String", "LIBRARY_VERSION", "\"${ProjectInfo.VERSION}\"")
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

fun GradleDokkaSourceSetBuilder.configureDokkaSourceSet(platform: String) {
    sourceLink {
        localDirectory.set(file("src/${platform}Main/kotlin"))
        remoteUrl.set(URL("${ProjectInfo.LOCATION_HTTP}/blob/main/tts/src/${platform}Main/kotlin"))
        remoteLineSuffix.set("#L")
    }

    externalDocumentationLink {
        url.set(URL("https://marc-jb.github.io/TextToSpeechKt/tts"))
        packageListUrl.set(URL("https://marc-jb.github.io/TextToSpeechKt/tts/package-list"))
    }

    if (platform == "android") {
        jdkVersion.set(JavaVersion.VERSION_1_8.majorVersion.toInt())
    }
}

val versionArchiveDirectory = file(buildDir.toPath().resolve("dokka").resolve("html_version_archive"))

val generateDokkaHtmlArchiveTasks by tasks.register<org.jetbrains.dokka.gradle.DokkaTask>("dokkaPreviouslyDocumentation") {
    dependencies {
        dokkaPlugin("org.jetbrains.dokka:versioning-plugin:1.8.10")
    }

    outputDirectory.set(file(versionArchiveDirectory.toPath().resolve(ProjectInfo.VERSION_MAJOR_MINOR)))

    val versioningPluginClass = "org.jetbrains.dokka.versioning.VersioningPlugin"
    val versioningPluginConfig = """{ "version": "${ProjectInfo.VERSION_MAJOR_MINOR}" }"""

    pluginsMapConfiguration.set(
        mapOf(
            versioningPluginClass to versioningPluginConfig
        )
    )

    configureAllSourceSets()
}

tasks.dokkaHtml {
    dependsOn(generateDokkaHtmlArchiveTasks)

    dependencies {
        dokkaPlugin("org.jetbrains.dokka:versioning-plugin:1.8.10")
    }

    val versionArchivePath = versionArchiveDirectory.toString().replace("\\", "\\\\")

    val versioningPluginClass = "org.jetbrains.dokka.versioning.VersioningPlugin"
    val versioningPluginConfig = """{ "version": "${ProjectInfo.VERSION_MAJOR_MINOR}", "olderVersionsDir": "$versionArchivePath" }"""

    pluginsMapConfiguration.set(
        mapOf(
            versioningPluginClass to versioningPluginConfig
        )
    )

    configureAllSourceSets()
}

fun org.jetbrains.dokka.gradle.DokkaTask.configureAllSourceSets() {
    dokkaSourceSets {
        named("commonMain") {
            configureDokkaSourceSet("common")
        }

        named("androidMain") {
            configureDokkaSourceSet("android")
        }

        named("browserMain") {
            configureDokkaSourceSet("browser")
        }
    }
}

val javadocJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
    dependsOn(tasks.dokkaHtml)
    archiveClassifier.set("javadoc")
    from(buildDir.toPath().resolve("dokka"))
}

fun MavenPublication.configurePublication() {
    groupId = ProjectInfo.GROUP_ID

    artifactId = "tts" + when {
        artifactId.endsWith("-android") || name == "android" -> "-android"
        artifactId.endsWith("-browser") -> "-browser"
        else -> ""
    }

    artifact(javadocJar.get())

    pom {
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
                if(ProjectInfo.SNAPSHOT) {
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
            configurePublication()
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

afterEvaluate {
    tasks.named("publishAndroidPublicationToOSSRHRepository").configure { mustRunAfter("signBrowserPublication") }
    tasks.named("publishAndroidPublicationToGitHubPackagesRepository").configure { mustRunAfter("signBrowserPublication") }
}
