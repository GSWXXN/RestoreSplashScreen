plugins {
    id("com.android.application") version "7.2.2" apply false
    kotlin("jvm") version "1.7.10" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}