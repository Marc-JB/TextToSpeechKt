import java.util.*

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("plugins")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "TextToSpeechKt"

include(":tts")
include(":tts-compose")
include(":demo:compose-multiplatform")
include(":demo:android-xml")

gradle.beforeProject {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        val localProperties = Properties()
        localPropertiesFile.inputStream().use {
            localProperties.load(it)
        }
        localProperties.forEach { (key, value) ->
            if (key is String) {
                extra.set(key, value)
            }
        }
    }
}
