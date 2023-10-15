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

val includeDemoProjects = true

if (includeDemoProjects) {
    include(":demo:compose-multiplatform")
    include(":demo:android-xml")
    include(":demo:browser-html-dom")
}
