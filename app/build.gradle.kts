plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.devtools.ksp") version "1.7.20-1.0.7"
}

android {
    namespace = "com.gswxxn.restoresplashscreen"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.gswxxn.restoresplashscreen"
        minSdk = 31
        targetSdk = 33
        versionCode = 251
        versionName = "2.5"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(mapOf("path" to ":blockmiui")))
    compileOnly("de.robv.android.xposed:api:82")
    implementation("com.highcapable.yukihookapi:api:1.1.4")
    ksp("com.highcapable.yukihookapi:ksp-xposed:1.1.4")

    implementation("androidx.palette:palette-ktx:1.0.0")
    implementation("androidx.compose.material3:material3:1.0.0-rc01")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
}

