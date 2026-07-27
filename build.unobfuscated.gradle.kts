import me.modmuss50.mpp.ReleaseType
import org.gradle.api.tasks.bundling.AbstractArchiveTask

plugins {
    id("dev.kikugie.stonecutter")
    id("net.fabricmc.fabric-loom") version "1.17.17" // non-remapping variant (Minecraft 26+ ships unobfuscated)
    id("me.modmuss50.mod-publish-plugin") version "2.1.1"
    kotlin("jvm") version "2.4.10"
    kotlin("plugin.serialization") version "2.4.10"
}

data class Unobf(val depends: String, val gameVersions: List<String>, val fapi: String, val modmenu: String, val yacl: String)

val mcVersion = stonecutter.current.version
val u = when (mcVersion) {
    "26.1.2" -> Unobf(">=26.1 <26.2", listOf("26.1", "26.1.1", "26.1.2"), "0.155.2+26.1.2", "18.0.0", "3.9.6+26.1-fabric")
    "26.2" -> Unobf(">=26.2 <27", listOf("26.2"), "0.155.2+26.2", "20.0.1", "3.9.6+26.2-fabric")
    else -> error("Unconfigured Minecraft version: $mcVersion")
}
val javaVersion = 25

version = "${property("mod_version")}+mc$mcVersion"
group = property("maven_group") as String
base { archivesName = property("archives_base_name") as String }

repositories {
    maven("https://maven.terraformersmc.com/releases/") // ModMenu
    maven("https://maven.nucleoid.xyz/")                 // placeholder-api (transitive of older ModMenu)
    maven("https://api.modrinth.com/maven")              // YACL (per-version, via Modrinth)
}

dependencies {
    minecraft("com.mojang:minecraft:$mcVersion")
    // No mappings() — Minecraft 26+ is unobfuscated (Mojang names), no Yarn/Mojmap.
    implementation("net.fabricmc:fabric-loader:${property("loader_version")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${u.fapi}")
    implementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")

    // Config screen deps are optional and NOT bundled: ModMenu hosts the screen, YACL renders it.
    // The screen no-ops when YACL is absent (see ModMenuIntegration).
    implementation("com.terraformersmc:modmenu:${u.modmenu}")
    implementation("maven.modrinth:yacl:${u.yacl}")

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
        "config_screen_mod" to "yet_another_config_lib_v3",
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

// Publish the non-remapped jar (unobfuscated 26+ has no remapJar).
publishMods {
    file = tasks.named<AbstractArchiveTask>("jar").flatMap { it.archiveFile }
    type = ReleaseType.STABLE
    modLoaders.add("fabric")
    version = project.version.toString()
    displayName = "Simple Player Heads ${project.version}"
    changelog = "See https://github.com/iSlavok/Simple-Player-Heads/releases"

    modrinth {
        projectId = providers.gradleProperty("modrinth_id")
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        minecraftVersions.addAll(u.gameVersions)
        requires("fabric-api")
        requires("fabric-language-kotlin")
        optional("modmenu")
    }
}
