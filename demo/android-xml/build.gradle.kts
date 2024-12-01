import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}

android {
    compileSdk = 35
    buildToolsVersion = "35.0.0"

    namespace = "nl.marc_apps.tts_demo"

    defaultConfig {
        applicationId = "nl.marc_apps.tts_demo"

        minSdk = 26
        targetSdk = 35

        versionCode = 1
        versionName = "0.1"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        viewBinding = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_1_8
    }
}

dependencies {
    implementation(projects.tts)

    implementation(libs.kotlin.coroutines)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.lifecycle.runtime)

    implementation(libs.google.material.xml)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.startup)

    implementation(platform(libs.koin.annotations.bom))
    implementation(libs.koin.annotations)
    ksp(libs.koin.annotations.ksp)
}
