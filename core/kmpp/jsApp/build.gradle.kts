import org.jetbrains.compose.compose

plugins {
    kotlin("js")
    id("org.jetbrains.compose") version "1.0.1-rc2"
}

dependencies {
    implementation(project(":libJs"))

    implementation(compose.web.core)
    implementation(compose.runtime)

    implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.6.0")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-extensions:1.0.1-pre.260-kotlin-1.5.31")

    implementation(devNpm("file-loader", "^6.1.0"))

    implementation(npm("firebase", "^8.3.1"))
    implementation(npm("firebaseui", "^4.8.0"))

    implementation(npm("bootstrap", "5.1.3"))
    implementation(npm("bootstrap-icons", "1.7.0"))
    implementation(npm("bootstrap-table", "1.18.3")) // add-ons not easy to enable.

    implementation(npm("sortablejs", "1.14.0"))

    api("io.kvision:jquery-kotlin:1.0.0")

    implementation(npm("jquery", "^3.5.1"))
    implementation(npm("@popperjs/core", "^2.10.2"))

    implementation(npm("mdb-ui-kit", "3.9.0"))

    implementation("org.webjars:font-awesome:5.15.2")

    testImplementation(kotlin("test-js"))
}

// NOTE: See https://kotlinlang.org/docs/reference/js-project-setup.html
//   Also found working examples here https://github.com/rjaros/kvision/blob/master/build.gradle.kts
kotlin {
    // NOTE: previously, js(IR) didn't support source-maps. Track progress below:
    //   https://youtrack.jetbrains.com/issue/KT-39447?_gl=1*133g0g5*_ga*NDkwNzI2NDM3LjE2MDMzNzE1MzE.*_ga_J6T75801PF*MTYyODU2NjI1NC4zLjEuMTYyODU2Nzc5MS4w&_ga=2.9886837.1974814920.1628566173-490726437.1603371531
    js(IR) {
        useCommonJs() // NOTE: 'Fix' vs UMD https://github.com/Kotlin/dukat/issues/106
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
            testTask {
                testLogging.showStandardStreams = true
            }
        }
    }
}

// Hotfix for webpack-cli bug. https://youtrack.jetbrains.com/issue/KT-49124
/*
rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin> {
    rootProject.the<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension>().apply {
        resolution("@webpack-cli/serve", "1.5.2")
    }
}
*/

tasks.register("copyAssemblyToFirebaseHosting", Copy::class){
    dependsOn("assemble")
    this.group = "firebase"
    from("build/distributions") {
        include("*.js", "*.map", "*.html", "**/*.css", "images/**/*", "json/**/*.json")
    }
    into("../../firebase/kmpp-template/public")
    doLast {
        println("Copy is done.")
    }
}
