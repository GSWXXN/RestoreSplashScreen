plugins {
    autowire(libs.plugins.com.android.application) apply false
    autowire(libs.plugins.org.jetbrains.kotlin.android) apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
