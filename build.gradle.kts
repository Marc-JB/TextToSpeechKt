buildscript {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        // TODO: Remove manual R8 declaration when AGP 7.1 releases: https://issuetracker.google.com/issues/206855609
        maven(url = "https://storage.googleapis.com/r8-releases/raw")
    }

    dependencies {
        // TODO: Remove manual R8 declaration when AGP 7.1 releases: https://issuetracker.google.com/issues/206855609
        classpath("com.android.tools:r8:3.1.42")
        classpath("com.android.tools.build:gradle:7.0.3")
        classpath(kotlin("gradle-plugin", "1.6.0"))
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.6.0")
    }
}
