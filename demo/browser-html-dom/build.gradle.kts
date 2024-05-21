plugins {
    kotlin("js")
    id("org.jetbrains.compose")
}

dependencies {
    implementation(project(":tts"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.7.3")

    implementation(compose.html.core)
    implementation(compose.runtime)

    testImplementation(kotlin("test"))
}

kotlin {
    js(IR) {
        binaries.executable()
        browser()
    }
}
