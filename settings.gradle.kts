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
        maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
    }
}

rootProject.name = "TextToSpeechKt"

include(":tts")
include(":tts-compose")
include(":demo:multiplatform-compose")
include(":demo:browser-html-dom")
include(":demo:android-xml")
