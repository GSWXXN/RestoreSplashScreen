enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
plugins {
    id("com.highcapable.sweetdependency") version "1.0.4"
    id("com.highcapable.sweetproperty") version "1.0.5"
}
sweetProperty {
    project(":app") {
        sourcesCode {
            isEnable = false
        }
        buildScript {
            extensionName = "property"
            propertiesFileNames(
                "local.properties",
                isAddDefault = true
            )
            permanentKeyValues(
                "KEYSTORE_PATH" to "",
                "KEYSTORE_PASS" to "",
                "KEY_ALIAS" to "",
                "KEY_PASSWORD" to ""
            )
            generateFrom(SYSTEM_ENV, ROOT_PROJECT, CURRENT_PROJECT)
        }
    }
}
rootProject.name = "RestoreSplashScreen"
include(":app", ":blockmiui")
include(":app", ":hyperx-compose")
