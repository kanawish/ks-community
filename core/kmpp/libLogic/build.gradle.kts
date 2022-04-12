/**
@startuml

package appAndroid
package libAndroid

package appJs
package libJs

package appJvmDesktop
package libJvmDesktop

package libLogic
package libShared

appJs -down-> libJs
appAndroid -down-> libAndroid
appJvmDesktop -down-> libJvmDesktop

libJs -down-> libLogic
libAndroid -down-> libLogic
libJvmDesktop -down-> libLogic

libLogic -down-> libShared

@enduml
 */

// NOTE: Inspired by https://github.com/MrAsterisco/Time/blob/master/build.gradle.kts

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.32"
}

group = "com.kanastruk.libLogic"
version = "1.0-SNAPSHOT"

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
                api(project(":libShared"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")

                implementation("io.insert-koin:koin-core:3.0.2")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val vanillaJvmMain by getting
        val vanillaJvmTest by getting
        val jsMain by getting
        val jsTest by getting
        val macosMain by getting
        val macosTest by getting
        val iosMain by getting
        val iosTest by getting
/*
        val androidMain by getting
        val androidTest by getting
*/
    }
}
