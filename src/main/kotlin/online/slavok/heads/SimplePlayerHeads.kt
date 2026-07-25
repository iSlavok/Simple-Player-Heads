package online.slavok.heads

import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import online.slavok.heads.config.ConfigManager

object SimplePlayerHeads : ModInitializer {
    lateinit var configManager: ConfigManager
        private set

    override fun onInitialize() {
        val configFile = FabricLoader.getInstance().configDir.resolve("simple-player-heads.toml").toFile()
        configManager = ConfigManager(configFile)
    }
}
