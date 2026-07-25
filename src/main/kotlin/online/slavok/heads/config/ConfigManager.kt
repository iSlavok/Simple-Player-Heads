package online.slavok.heads.config

import net.peanuuutz.tomlkt.Toml
import org.slf4j.LoggerFactory
import java.io.File

/**
 * Loads and persists the mod config. On a missing or malformed file it falls back to defaults
 * and (re)writes a valid file instead of crashing.
 */
class ConfigManager(private val configFile: File) {
    var config: Config = Config()
        private set

    init {
        load()
    }

    private fun load() {
        config = if (configFile.exists()) {
            try {
                Toml.decodeFromString(Config.serializer(), configFile.readText())
            } catch (e: Exception) {
                LOGGER.error("Failed to parse {}, using defaults", configFile.name, e)
                Config()
            }
        } else {
            Config()
        }
        // Normalize the file: create it if missing, add any new fields with comments.
        save()
    }

    /** Replaces the active config and persists it to disk. */
    fun save(newConfig: Config = config) {
        config = newConfig
        try {
            configFile.parentFile?.mkdirs()
            configFile.writeText(Toml.encodeToString(Config.serializer(), config))
        } catch (e: Exception) {
            LOGGER.error("Failed to write {}", configFile.name, e)
        }
    }

    private companion object {
        val LOGGER: org.slf4j.Logger = LoggerFactory.getLogger("simple-player-heads")
    }
}
