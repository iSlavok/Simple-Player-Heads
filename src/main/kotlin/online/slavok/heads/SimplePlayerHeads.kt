package online.slavok.heads

import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import online.slavok.heads.config.ConfigManager

object SimplePlayerHeads : ModInitializer {
	val configManager = ConfigManager(FabricLoader.getInstance().configDir.resolve("simple-player-heads.toml").toFile())

	override fun onInitialize() {
	}
}