pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "core"

include(":androidApp")
include(":desktopApp")
include(":jsApp")
include(":libJs")
include(":libAndroid")
include(":libLogic")
include(":libShared")
