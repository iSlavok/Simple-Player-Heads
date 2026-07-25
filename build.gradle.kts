import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("dev.kikugie.stonecutter")
    id("fabric-loom") version "1.17.17"
    kotlin("jvm") version "2.4.10"
    kotlin("plugin.serialization") version "2.4.10"
}

// Per-version matrix. `fapi` (Fabric API) is used by the main mod and the gametest source set.
data class Mc(
    val yarn: String,
    val java: Int,
    val depends: String,
    val gameVersions: List<String>,
    val fapi: String,
)

val mcVersion = stonecutter.current.version
val mc = when (mcVersion) {
    "1.18.2" -> Mc("1.18.2+build.4", 17, ">=1.18.2 <1.19", listOf("1.18.2"), "0.77.0+1.18.2")
    "1.19.2" -> Mc("1.19.2+build.28", 17, ">=1.19 <1.19.3", listOf("1.19", "1.19.1", "1.19.2"), "0.77.0+1.19.2")
    "1.19.4" -> Mc("1.19.4+build.2", 17, ">=1.19.3 <1.20", listOf("1.19.3", "1.19.4"), "0.87.2+1.19.4")
    "1.20.1" -> Mc("1.20.1+build.10", 17, ">=1.20 <1.20.2", listOf("1.20", "1.20.1"), "0.92.11+1.20.1")
    "1.20.4" -> Mc("1.20.4+build.3", 17, ">=1.20.2 <1.20.5", listOf("1.20.2", "1.20.3", "1.20.4"), "0.97.3+1.20.4")
    "1.20.6" -> Mc("1.20.6+build.3", 21, ">=1.20.5 <1.21", listOf("1.20.5", "1.20.6"), "0.100.8+1.20.6")
    "1.21.8" -> Mc(
        "1.21.8+build.1", 21, ">=1.21 <1.22",
        listOf("1.21", "1.21.1", "1.21.2", "1.21.3", "1.21.4", "1.21.5", "1.21.6", "1.21.7", "1.21.8"),
        "0.136.1+1.21.8",
    )
    else -> error("Unconfigured Minecraft version: $mcVersion")
}

version = "${property("mod_version")}+mc$mcVersion"
group = property("maven_group") as String
base { archivesName = property("archives_base_name") as String }

dependencies {
    minecraft("com.mojang:minecraft:$mcVersion")
    mappings("net.fabricmc:yarn:${mc.yarn}:v2")
    modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${mc.fapi}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")

    // kotlinx-serialization-json is provided at runtime by Fabric Language Kotlin.
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
    // tomlkt is not bundled by FLK, so ship it inside the jar (jar-in-jar).
    implementation("net.peanuuutz.tomlkt:tomlkt:0.4.0")
    include("net.peanuuutz.tomlkt:tomlkt:0.4.0")
}

tasks.processResources {
    val props = mapOf(
        "version" to project.version,
        "java_level" to mc.java,
        "minecraft_dep" to mc.depends,
    )
    inputs.properties(props)
    filesMatching(listOf("fabric.mod.json", "*.mixins.json")) { expand(props) }
}

// Cross-compile the per-version Java level (17 for <=1.20.4, 21 for 1.20.5+) from a
// single JDK 21 build. Gametests, which must *run* on the matching JDK, switch this to
// per-version toolchains in a later PR.
tasks.withType<JavaCompile>().configureEach { options.release = mc.java }

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget(mc.java.toString())
    }
}

java {
    withSourcesJar()
    val jv = JavaVersion.toVersion(mc.java)
    sourceCompatibility = jv
    targetCompatibility = jv
}
