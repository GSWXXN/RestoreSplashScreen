plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.devtools.ksp") version "1.7.0-1.0.6"
}

android {
    namespace = "com.gswxxn.restoresplashscreen"
    compileSdk = 32

    defaultConfig {
        applicationId = "com.gswxxn.restoresplashscreen"
        minSdk = 31
        targetSdk = 32
        versionCode = 221
        versionName = "2.2.1"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        viewBinding = true
    }

    applicationVariants.all {
        val buildType = buildType.name
        outputs.all {
            if (this is com.android.build.gradle.internal.api.ApkVariantOutputImpl) {
                this.outputFileName = "${if (versionName.contains("Preview") && buildType != "debug") "MIUI遮罩进化" else "RestoreSplashScreen"}_${versionName}${if (buildType == "debug") "_debug" else ""}.apk"
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(project(mapOf("path" to ":blockmiui")))
    compileOnly("de.robv.android.xposed:api:82")
    implementation("com.highcapable.yukihookapi:api:1.0.92")
    ksp("com.highcapable.yukihookapi:ksp-xposed:1.0.92")

    implementation("com.github.topjohnwu.libsu:core:5.0.2")
    implementation("androidx.palette:palette-ktx:1.0.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.3")
}

