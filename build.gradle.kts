plugins {
    val androidVersion = "7.4.2"
    val kotlinVersion = "1.8.10"

    id("com.android.application") version androidVersion apply false
    id("com.android.library") version androidVersion apply false

    kotlin("multiplatform") version kotlinVersion apply false
    id("org.jetbrains.dokka") version kotlinVersion apply false

    id("org.jetbrains.compose") version "1.3.1" apply false
}
