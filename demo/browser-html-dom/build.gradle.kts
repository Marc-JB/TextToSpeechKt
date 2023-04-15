plugins {
    kotlin("js")
    id("org.jetbrains.compose") version "1.3.1"
}

dependencies {
    implementation(project(":tts"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.6.4")

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
