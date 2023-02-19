plugins {
    val androidVersion = "7.2.2"
    val kotlinVersion = "1.7.20"

    id("com.android.application") version androidVersion apply false
    id("com.android.library") version androidVersion apply false

    kotlin("multiplatform") version kotlinVersion apply false
    id("org.jetbrains.dokka") version kotlinVersion apply false

    id("org.jetbrains.compose") version "1.2.1" apply false
}
