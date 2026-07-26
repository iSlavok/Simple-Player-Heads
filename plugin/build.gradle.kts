plugins {
    kotlin("jvm") version "2.4.10"
    id("com.gradleup.shadow") version "9.6.0"
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

group = providers.gradleProperty("maven_group").get()
version = providers.gradleProperty("plugin_version").get()

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
