plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.32"
}

group = "com.kanastruk.libShared"
version = "1.0-SNAPSHOT"

// https://stackoverflow.com/questions/59479020/how-to-share-java-code-between-android-and-jvm-targets-with-kotlin-multiplatfor
// re: apparent jvm conflict.

android {
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    compileSdk = 31
    defaultConfig {
        minSdk = 24
        targetSdk = 31
    }
    // ...
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
}

kotlin {
    // TODO: Read up on https://kotlinlang.org/docs/mpp-set-up-targets.html#distinguish-several-targets-for-one-platform
    jvm("vanillaJvm") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
        binaries.executable()
    }
    ios()
    macosX64("macos")

    android() // ...?

    sourceSets {
        all {
            languageSettings.apply {
                optIn("kotlin.RequiresOptIn")
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
            }
        }
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")

                implementation("io.insert-koin:koin-core:3.0.2")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.insert-koin:koin-test:3.0.2")
            }
        }

        val androidMain by getting {}
        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
            }
        }

        val iosMain by getting {}
        val iosTest by getting {}

        val vanillaJvmMain by getting {}
        val vanillaJvmTest by getting {}

        val jsMain by getting {}
        val jsTest by getting {}

    }
}
