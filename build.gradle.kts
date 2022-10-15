plugins {
    id("com.android.application") version "7.3.0" apply false
    kotlin("jvm") version "1.7.20" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
