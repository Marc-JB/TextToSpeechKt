plugins {
    val kotlinVersion = "1.6.20"

    id("com.android.application") version "7.1.3" apply false
    kotlin("multiplatform") version kotlinVersion apply false
    id("org.jetbrains.dokka") version kotlinVersion apply false
}
