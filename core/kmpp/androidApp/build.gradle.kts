plugins {
    id("com.android.application")

    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")

    kotlin("android")
}

val composeVersion = "1.1.0-rc01"

android {
    compileSdk = 31
    defaultConfig {
        applicationId = "com.kanastruk.androidApp"
        minSdk = 24
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.1.0-rc02"
    }
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn" + "-Xmulti-platform"
    }
}

dependencies {

    implementation(project(":libAndroid"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")

    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("androidx.core:core-ktx:1.7.0")


    // Compose support
    implementation("androidx.compose.ui:ui:${composeVersion}")
    // Tooling support (Previews, etc.)
    implementation("androidx.compose.ui:ui-tooling:${composeVersion}")
    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    implementation("androidx.compose.foundation:foundation:${composeVersion}")
    // Material Design
    implementation("androidx.compose.material:material:${composeVersion}")
    // Material design icons
    implementation("androidx.compose.material:material-icons-core:${composeVersion}")
    implementation("androidx.compose.material:material-icons-extended:${composeVersion}")

    // Compose 'extras'
    implementation("androidx.activity:activity-compose:1.4.0")

    implementation("com.google.android.material:material:1.5.0")

    // Logging
    implementation("com.jakewharton.timber:timber:4.7.1") // https://github.com/JakeWharton/timber/releases

    // Networking
    implementation("com.squareup.okhttp3:okhttp:4.9.0") // https://square.github.io/okhttp/changelog/
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation("com.squareup.okhttp3:okhttp-urlconnection:4.9.0")
    implementation("com.squareup.retrofit2:retrofit:2.8.1") // https://github.com/square/retrofit/blob/master/CHANGELOG.md
    implementation("com.squareup.retrofit2:converter-gson:2.8.1")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.8.1")

    // DI
    implementation("io.insert-koin:koin-android:3.0.2")

    // Image loading
    implementation("io.coil-kt:coil:1.3.2") // https://github.com/coil-kt/coil/releases

    // Unit Tests
    testImplementation("com.nhaarman:mockito-kotlin-kt1.1:1.5.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.insert-koin:koin-test:3.0.2")

    // Instrumentation Tests
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    // UI Tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.0.5")

}

