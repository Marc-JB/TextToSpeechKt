pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
        jcenter()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "com.android" || requested.id.name == "kotlin-android-extensions") {
                useModule("com.android.tools.build:gradle:3.6.3")
            }
        }
    }
}

rootProject.name = "TextToSpeechKt"

enableFeaturePreview("GRADLE_METADATA")
