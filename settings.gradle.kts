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

// Standalone plugin build — surfaced to the IDE only; it is NOT pulled into the root
// `build` (which would configure the whole Loom/Minecraft matrix). Build it with
// `./gradlew -p plugin build`.
includeBuild("plugin")

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"
    create(rootProject) {
        versions("1.18.1", "1.18.2", "1.19.2", "1.19.4", "1.20.1", "1.20.4", "1.20.6", "1.21.8")
        // Unobfuscated 26+ needs JDK 25 and a separate (non-remapping) build script.
        // Register it only when the running JDK can build it, so the project still
        // builds the yarn range on older JDKs.
        if (JavaVersion.current().majorVersion.toInt() >= 25) {
            // 26.x drops are not binary-compatible with each other, so each gets its own anchor.
            version("26.1.2", "26.1.2").buildscript = "build.unobfuscated.gradle.kts"
            version("26.2", "26.2").buildscript = "build.unobfuscated.gradle.kts"
        }
        vcsVersion = "1.21.8"
    }
}

rootProject.name = "simple-player-heads"
