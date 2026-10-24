plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "nl.marc_apps.tts.demo"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "nl.marc_apps.tts.demo"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
}

dependencies {
    implementation(projects.demo.shared)

    coreLibraryDesugaring(libs.androidx.desugar.jdk.libs)
}
