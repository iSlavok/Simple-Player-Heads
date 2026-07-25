import me.modmuss50.mpp.ReleaseType
import org.gradle.api.tasks.bundling.AbstractArchiveTask

plugins {
    id("dev.kikugie.stonecutter")
    id("fabric-loom") version "1.17.17"
    id("me.modmuss50.mod-publish-plugin") version "2.1.1"
    kotlin("jvm") version "2.4.10"
    kotlin("plugin.serialization") version "2.4.10"
}

// Per-version matrix. `fapi` (Fabric API) is used by the main mod and the gametest source set.
// `cloth` / `modmenu` power the optional in-game config screen.
data class Mc(
    val yarn: String,
    val java: Int,
    val depends: String,
    val gameVersions: List<String>,
    val fapi: String,
    val cloth: String,
    val modmenu: String,
)

val mcVersion = stonecutter.current.version
val mc = when (mcVersion) {
    // 1.18.2 is a separate intermediary generation from 1.18/1.18.1, so 1.18 + 1.18.1 need their own anchor.
    "1.18.1" -> Mc("1.18.1+build.22", 17, ">=1.18 <1.18.2", listOf("1.18", "1.18.1"), "0.46.6+1.18", "6.5.102", "3.0.1")
    "1.18.2" -> Mc("1.18.2+build.4", 17, ">=1.18.2 <1.19", listOf("1.18.2"), "0.77.0+1.18.2", "6.5.102", "3.2.5")
    "1.19.2" -> Mc("1.19.2+build.28", 17, ">=1.19 <1.19.3", listOf("1.19", "1.19.1", "1.19.2"), "0.77.0+1.19.2", "8.3.134", "4.1.2")
    "1.19.4" -> Mc("1.19.4+build.2", 17, ">=1.19.3 <1.20", listOf("1.19.3", "1.19.4"), "0.87.2+1.19.4", "10.1.135", "6.3.1")
    "1.20.1" -> Mc("1.20.1+build.10", 17, ">=1.20 <1.20.2", listOf("1.20", "1.20.1"), "0.92.11+1.20.1", "11.1.136", "7.2.2")
    "1.20.4" -> Mc("1.20.4+build.3", 17, ">=1.20.2 <1.20.5", listOf("1.20.2", "1.20.3", "1.20.4"), "0.97.3+1.20.4", "13.0.138", "9.2.0")
    "1.20.6" -> Mc("1.20.6+build.3", 21, ">=1.20.5 <1.21", listOf("1.20.5", "1.20.6"), "0.100.8+1.20.6", "14.0.139", "10.0.0")
    "1.21.8" -> Mc(
        "1.21.8+build.1", 21, ">=1.21 <1.22",
        // One 1.21.x jar runs on every 1.21.x patch; list them all so Modrinth shows them.
        listOf("1.21", "1.21.1", "1.21.2", "1.21.3", "1.21.4", "1.21.5", "1.21.6", "1.21.7", "1.21.8", "1.21.9", "1.21.10", "1.21.11"),
        "0.136.1+1.21.8", "19.0.147", "15.0.2",
    )
    else -> error("Unconfigured Minecraft version: $mcVersion")
}

version = "${property("mod_version")}+mc$mcVersion"
group = property("maven_group") as String
base { archivesName = property("archives_base_name") as String }

repositories {
    maven("https://maven.terraformersmc.com/releases/") // ModMenu
    maven("https://maven.shedaniel.me/")                 // Cloth Config
    maven("https://maven.nucleoid.xyz/")                 // placeholder-api (transitive of older ModMenu)
}

dependencies {
    minecraft("com.mojang:minecraft:$mcVersion")
    mappings("net.fabricmc:yarn:${mc.yarn}:v2")
    modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${mc.fapi}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")

    // Config screen: ModMenu is optional (a "suggests"), so compile-only. Cloth Config
    // is bundled (jar-in-jar) so the screen works without the user installing it.
    modImplementation("com.terraformersmc:modmenu:${mc.modmenu}")
    modImplementation("me.shedaniel.cloth:cloth-config-fabric:${mc.cloth}") {
        exclude(group = "net.fabricmc.fabric-api")
    }
    include("me.shedaniel.cloth:cloth-config-fabric:${mc.cloth}")

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
    "modGametestImplementation"("net.fabricmc.fabric-api:fabric-api:${mc.fapi}")
}

// 1.18/1.18.1 only ship the old Fabric API (0.46.6), whose transitive access-widener
// clashes with the modern loader at gametest launch (NoClassDefFoundError:
// AccessWidenerVisitor). The gametest still *compiles* here, and the logic is identical
// to 1.18.2 (same <1.19 branch), which is gametested — so build-verify 1.18.1 and skip
// only its gametest *run*.
if (mcVersion == "1.18.1") {
    tasks.matching { it.name == "runGameTest" }.configureEach { enabled = false }
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

// One JDK toolchain per Minecraft version (17 for <=1.20.4, 21 for 1.20.5+) so both
// compilation and gametests run on the JDK that Minecraft version requires.
java {
    toolchain.languageVersion = JavaLanguageVersion.of(mc.java)
    withSourcesJar()
}

kotlin {
    jvmToolchain(mc.java)
}

// Each version node publishes itself as one Modrinth version (`./gradlew publishMods`).
publishMods {
    file = tasks.named<AbstractArchiveTask>("remapJar").flatMap { it.archiveFile }
    type = ReleaseType.STABLE
    modLoaders.add("fabric")
    version = project.version.toString()
    displayName = "Simple Player Heads ${project.version}"
    changelog = "See https://github.com/iSlavok/Simple-Player-Heads/releases"

    modrinth {
        projectId = providers.gradleProperty("modrinth_id")
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        minecraftVersions.addAll(mc.gameVersions)
        requires("fabric-api")
        requires("fabric-language-kotlin")
        optional("modmenu")
    }
}
