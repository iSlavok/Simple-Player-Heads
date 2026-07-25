plugins {
    id("dev.kikugie.stonecutter")
    id("net.fabricmc.fabric-loom") version "1.17.17" // non-remapping variant (Minecraft 26+ ships unobfuscated)
    kotlin("jvm") version "2.4.10"
    kotlin("plugin.serialization") version "2.4.10"
}

data class Unobf(val depends: String, val gameVersions: List<String>, val fapi: String)

val mcVersion = stonecutter.current.version
val u = when (mcVersion) {
    "26.2" -> Unobf(">=26.2 <27", listOf("26.2"), "0.155.2+26.2")
    else -> error("Unconfigured Minecraft version: $mcVersion")
}
val javaVersion = 25

version = "${property("mod_version")}+mc$mcVersion"
group = property("maven_group") as String
base { archivesName = property("archives_base_name") as String }

dependencies {
    minecraft("com.mojang:minecraft:$mcVersion")
    // No mappings() — Minecraft 26+ is unobfuscated (Mojang names), no Yarn/Mojmap.
    implementation("net.fabricmc:fabric-loader:${property("loader_version")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${u.fapi}")
    implementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")

    // kotlinx-serialization-json is provided at runtime by Fabric Language Kotlin.
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
    // tomlkt is not bundled by FLK, so ship it inside the jar (jar-in-jar).
    implementation("net.peanuuutz.tomlkt:tomlkt:0.4.0")
    include("net.peanuuutz.tomlkt:tomlkt:0.4.0")
}

fabricApi {
    configureTests {
        createSourceSet = true
        modId = "${property("archives_base_name")}-test"
        eula = true
    }
}

dependencies {
    // Non-remapping variant: no "mod" prefix, no remap.
    "gametestImplementation"("net.fabricmc.fabric-api:fabric-api:${u.fapi}")
}

tasks.processResources {
    val props = mapOf(
        "version" to project.version,
        "java_level" to javaVersion,
        "minecraft_dep" to u.depends,
    )
    inputs.properties(props)
    filesMatching(listOf("fabric.mod.json", "*.mixins.json")) { expand(props) }
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
    withSourcesJar()
}

kotlin {
    jvmToolchain(javaVersion)
}
