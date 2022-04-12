buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
        classpath("com.android.tools.build:gradle:7.0.0")

        classpath("com.google.gms:google-services:4.3.10")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.8.1")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

tasks.register("firebaseVersion", Exec::class) {
    this.group = "firebase"
    workingDir = File("../../firebase")
    commandLine = "firebase --version".split(" ")
}

tasks.register("firebaseProjectList", Exec::class) {
    this.group = "firebase"
    workingDir = File("../../firebase")
    commandLine = "firebase projects:list".split(" ")
}
