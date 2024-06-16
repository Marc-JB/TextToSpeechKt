import java.util.*

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
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
include(":demo:browser-html-dom")

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
