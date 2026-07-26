import me.modmuss50.mpp.ReleaseType

plugins {
    kotlin("jvm") version "2.4.10"
    id("com.gradleup.shadow") version "9.6.0"
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("me.modmuss50.mod-publish-plugin") version "2.1.1"
}

group = providers.gradleProperty("maven_group").get()
// On a tag release the workflow passes -Pmod_version (same as the mod build); fall back to the
// local plugin_version default otherwise.
version = providers.gradleProperty("mod_version")
    .getOrElse(providers.gradleProperty("plugin_version").get())

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") // spigot-api
    maven("https://repo.papermc.io/repository/maven-public/")               // paper-api (tests)
    maven("https://oss.sonatype.org/content/repositories/snapshots/")       // spigot-api transitives
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:${property("spigot_api_version")}")
    implementation(kotlin("stdlib")) // shaded into the plugin jar (server has no Kotlin runtime)

    testImplementation("io.papermc.paper:paper-api:${property("paper_api_version")}")
    testImplementation("org.mockbukkit.mockbukkit:mockbukkit-v1.21:${property("mockbukkit_version")}")
    testImplementation("org.junit.jupiter:junit-jupiter:${property("junit_version")}")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}

// Shaded jar is the artifact servers load; keep the thin jar out of its way.
tasks.jar {
    archiveClassifier.set("thin")
}
tasks.shadowJar {
    archiveClassifier.set("")
}
tasks.build {
    dependsOn(tasks.shadowJar)
}

// Inject the project version into plugin.yml.
tasks.processResources {
    val props = mapOf("version" to project.version)
    inputs.properties(props)
    filesMatching("plugin.yml") { expand(props) }
}

tasks.runServer {
    val mc = providers.gradleProperty("run_mc").getOrElse("1.21.8")
    minecraftVersion(mc)
    // Paper for MC 26+ requires Java 25; older run on 21.
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(if (mc.substringBefore(".").toInt() >= 26) 25 else 21))
    })
}

// Publish the shaded jar to the SAME Modrinth project as the mod, tagged with the plugin loaders.
// The version string gets a distinct "+plugin" suffix so it never collides with the mod's
// per-Minecraft-version releases (Modrinth version numbers must be unique per project).
publishMods {
    // No token (local) → dry run: writes the payload under build/ without uploading.
    // CI provides MODRINTH_TOKEN, so a tagged release publishes for real.
    dryRun = providers.environmentVariable("MODRINTH_TOKEN").map { false }.orElse(true)
    file = tasks.shadowJar.flatMap { it.archiveFile }
    type = ReleaseType.STABLE
    modLoaders.addAll("bukkit", "spigot", "paper", "purpur", "folia")
    version = "${project.version}+plugin"
    displayName = "Simple Player Heads ${project.version} (Plugin)"
    changelog = "See https://github.com/iSlavok/Simple-Player-Heads/releases"

    modrinth {
        projectId = providers.gradleProperty("modrinth_id")
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        // One Bukkit-API jar spans the whole range; let mod-publish-plugin resolve the concrete
        // version list from Modrinth at publish time. The plugin has no dependencies.
        minecraftVersionRange {
            start = "1.18"
            end = "26.2"
        }
    }
}
