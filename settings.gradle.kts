import java.util.*

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
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
