pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie" }
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.7"
}

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"
    create(rootProject) {
        versions("1.18.2", "1.19.2", "1.19.4", "1.20.1", "1.20.4", "1.20.6", "1.21.8")
        vcsVersion = "1.21.8"
    }
}

rootProject.name = "simple-player-heads"
