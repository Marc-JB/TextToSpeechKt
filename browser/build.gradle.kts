plugins {
    kotlin("js")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-html:0.7.3")
    implementation(project(":tts"))

    testImplementation(kotlin("test"))
}

kotlin {
    js(IR) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }
}
