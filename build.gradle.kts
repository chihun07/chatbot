// Top-level build file (can be mostly empty if pluginManagement is in settings.gradle.kts)

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
