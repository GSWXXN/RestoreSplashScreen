plugins {
    id("com.android.application") version "7.4.0-beta02" apply false
    kotlin("jvm") version "1.7.20" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}