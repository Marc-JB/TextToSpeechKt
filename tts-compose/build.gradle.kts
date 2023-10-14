@file:Suppress("UnstableApiUsage")

import com.android.repository.Revision
import org.jetbrains.dokka.gradle.DokkaTaskPartial
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.konan.properties.Properties
import org.jetbrains.kotlin.util.capitalizeDecapitalize.toUpperCaseAsciiOnly
import java.net.URL

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    `maven-publish`
    signing
    id("org.jetbrains.compose")
    id("org.jetbrains.dokka")
}

object ProjectInfo {
    const val GROUP_ID = "nl.marc-apps"

    const val ID = "tts-compose"

    const val NAME = "TextToSpeechKt"

    val version = Revision(2, 0)

    val mavenVersion = "${version.major}.${version.minor}.${version.micro}${if (version.isPreview) "-SNAPSHOT" else ""}"

    object Developer {
        const val ORG_NAME = "Marc Apps & Software"

        const val WEBSITE = "https://marc-apps.nl"

        const val NAME = "Marc"

        const val EMAIL = "16156117+Marc-JB@users.noreply.github.com"

        const val GITHUB_NAME = "Marc-JB"
    }

    const val LOCATION = "github.com/${Developer.GITHUB_NAME}/TextToSpeechKt"

    const val LOCATION_HTTP = "https://$LOCATION"

    const val DOCUMENTATION_URL = "https://marc-jb.github.io/TextToSpeechKt"
}

val config by lazy { Config() }

group = ProjectInfo.GROUP_ID
version = ProjectInfo.mavenVersion

kotlin {
    js("browser", IR) {
        browser()

        binaries.executable()
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
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                api(project(":tts"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(compose.foundation)
            }
        }
    }
}

android {
    compileSdk = 34
    buildToolsVersion = "34.0.0"

    namespace = "nl.marc_apps.tts_compose"

    defaultConfig {
        minSdk = 21

        setProperty("archivesBaseName", ProjectInfo.ID)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

dependencies {
    dokkaPlugin("org.jetbrains.dokka:android-documentation-plugin:1.9.0")
    dokkaPlugin("org.jetbrains.dokka:versioning-plugin:1.9.0")
}

tasks.withType<DokkaTaskPartial>().configureEach {
    dokkaSourceSets.configureEach {
        val platform = when(name){
            "commonMain" -> "common"
            "androidMain" -> "android"
            "browserMain" -> "browser"
            "desktopMain" -> "desktop"
            else -> null
        }

        if (platform != null) {
            sourceLink {
                localDirectory.set(file("src/${platform}Main/kotlin"))
                remoteUrl.set(URL("${ProjectInfo.LOCATION_HTTP}/blob/main/${ProjectInfo.ID}/src/${platform}Main/kotlin"))
                remoteLineSuffix.set("#L")
            }

            externalDocumentationLink {
                url.set(URL("${ProjectInfo.DOCUMENTATION_URL}/${ProjectInfo.ID}"))
                packageListUrl.set(URL("${ProjectInfo.DOCUMENTATION_URL}/package-list"))
            }

            jdkVersion.set(JavaVersion.VERSION_1_8.majorVersion.toInt())
        }
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
                if(ProjectInfo.version.isPreview) {
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
    groupId = ProjectInfo.GROUP_ID

    artifactId = ProjectInfo.ID + when {
        artifactId.endsWith("-android") || name == "android" -> "-android"
        artifactId.endsWith("-browser") -> "-browser"
        artifactId.endsWith("-desktop") -> "-desktop"
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
    tasks.named("publishAndroidPublicationToOSSRHRepository").configure { mustRunAfter("signBrowserPublication") }
    tasks.named("publishAndroidPublicationToGitHubPackagesRepository").configure { mustRunAfter("signBrowserPublication") }
    tasks.named("publishAndroidPublicationToOSSRHRepository").configure { mustRunAfter("signKotlinMultiplatformPublication") }
    tasks.named("publishAndroidPublicationToGitHubPackagesRepository").configure { mustRunAfter("signKotlinMultiplatformPublication") }
    tasks.named("publishAndroidPublicationToOSSRHRepository").configure { mustRunAfter("signDesktopPublication") }
    tasks.named("publishAndroidPublicationToGitHubPackagesRepository").configure { mustRunAfter("signDesktopPublication") }

    tasks.named("publishBrowserPublicationToOSSRHRepository").configure { mustRunAfter("signAndroidPublication") }
    tasks.named("publishBrowserPublicationToGitHubPackagesRepository").configure { mustRunAfter("signAndroidPublication") }
    tasks.named("publishBrowserPublicationToOSSRHRepository").configure { mustRunAfter("signKotlinMultiplatformPublication") }
    tasks.named("publishBrowserPublicationToGitHubPackagesRepository").configure { mustRunAfter("signKotlinMultiplatformPublication") }
    tasks.named("publishBrowserPublicationToOSSRHRepository").configure { mustRunAfter("signDesktopPublication") }
    tasks.named("publishBrowserPublicationToGitHubPackagesRepository").configure { mustRunAfter("signDesktopPublication") }

    tasks.named("publishKotlinMultiplatformPublicationToOSSRHRepository").configure { mustRunAfter("signAndroidPublication") }
    tasks.named("publishKotlinMultiplatformPublicationToGitHubPackagesRepository").configure { mustRunAfter("signAndroidPublication") }
    tasks.named("publishKotlinMultiplatformPublicationToOSSRHRepository").configure { mustRunAfter("signBrowserPublication") }
    tasks.named("publishKotlinMultiplatformPublicationToGitHubPackagesRepository").configure { mustRunAfter("signBrowserPublication") }
    tasks.named("publishKotlinMultiplatformPublicationToOSSRHRepository").configure { mustRunAfter("signDesktopPublication") }
    tasks.named("publishKotlinMultiplatformPublicationToGitHubPackagesRepository").configure { mustRunAfter("signDesktopPublication") }

    tasks.named("publishDesktopPublicationToOSSRHRepository").configure { mustRunAfter("signAndroidPublication") }
    tasks.named("publishDesktopPublicationToGitHubPackagesRepository").configure { mustRunAfter("signAndroidPublication") }
    tasks.named("publishDesktopPublicationToOSSRHRepository").configure { mustRunAfter("signBrowserPublication") }
    tasks.named("publishDesktopPublicationToGitHubPackagesRepository").configure { mustRunAfter("signBrowserPublication") }
    tasks.named("publishDesktopPublicationToOSSRHRepository").configure { mustRunAfter("signKotlinMultiplatformPublication") }
    tasks.named("publishDesktopPublicationToGitHubPackagesRepository").configure { mustRunAfter("signKotlinMultiplatformPublication") }
}
