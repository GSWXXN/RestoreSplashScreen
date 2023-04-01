plugins {
    id("com.android.application") version "7.4.1" apply false
    kotlin("jvm") version "1.8.10" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
