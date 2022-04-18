plugins {
    kotlin("js")
    id("org.jetbrains.compose") version "1.1.1"
}

dependencies {
    implementation(project(":tts"))

    implementation(compose.web.core)
    implementation(compose.runtime)

    testImplementation(kotlin("test"))
}

kotlin {
    js(IR) {
        binaries.executable()
        browser()
    }
}
