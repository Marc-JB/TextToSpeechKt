buildscript {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.0")
        classpath("com.android.tools.build:gradle:7.0.3")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.6.0")
    }
}
