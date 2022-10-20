import java.util.Properties

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
        versionName = "2.5.1"
    }

    val properties = Properties()
    runCatching { properties.load(project.rootProject.file("local.properties").inputStream()) }
    val keystorePath = properties.getProperty("KEYSTORE_PATH") ?: System.getenv("KEYSTORE_PATH")
    val keystorePwd = properties.getProperty("KEYSTORE_PASS") ?: System.getenv("KEYSTORE_PASS")
    val alias = properties.getProperty("KEY_ALIAS") ?: System.getenv("KEY_ALIAS")
    val pwd = properties.getProperty("KEY_PASSWORD") ?: System.getenv("KEY_PASSWORD")
    if (keystorePath != null) {
        signingConfigs {
            create("release") {
                storeFile = file(keystorePath)
                storePassword = keystorePwd
                keyAlias = alias
                keyPassword = pwd
                enableV3Signing = true
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            if (keystorePath != null) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    flavorDimensionList.add("tier")
    productFlavors {
        create("CI") {
            dimension = "tier"
            versionCode = defaultConfig.versionCode?.plus(1)
            versionName = "${defaultConfig.versionName?.split(" - ")?.get(0)}${getGitHeadRefsSuffix(rootProject)}"
        }
        create("app") {
            dimension = "tier"
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
    implementation("com.highcapable.yukihookapi:api:1.1.4")
    ksp("com.highcapable.yukihookapi:ksp-xposed:1.1.4")

    implementation("androidx.palette:palette-ktx:1.0.0")
    implementation("androidx.compose.material3:material3:1.0.0-rc01")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
}

// from https://github.com/qqlittleice/MiuiHome_R/blob/main/app/build.gradle.kts
fun getGitHeadRefsSuffix(project: Project): String {
    // .git/HEAD描述当前目录所指向的分支信息，内容示例："ref: refs/heads/master\n"
    val headFile = File(project.rootProject.projectDir, ".git" + File.separator + "HEAD")
    if (headFile.exists()) {
        val string: String = headFile.readText(Charsets.UTF_8)
        val string1 = string.replace(Regex("""ref:|\s"""), "")
        val result = if (string1.isNotBlank() && string1.contains('/')) {
            val refFilePath = ".git" + File.separator + string1
            // 根据HEAD读取当前指向的hash值，路径示例为：".git/refs/heads/master"
            val refFile = File(project.rootProject.projectDir, refFilePath)
            // 索引文件内容为hash值+"\n"，
            // 示例："90312cd9157587d11779ed7be776e3220050b308\n"
            refFile.readText(Charsets.UTF_8).replace(Regex("""\s"""), "").subSequence(0, 7)
        } else {
            string.substring(0, 7)
        }
        println("commit_id: $result")
        return "-CI.$result"
    } else {
        println("WARN: .git/HEAD does NOT exist")
        return ""
    }
}
