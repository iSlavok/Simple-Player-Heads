package online.slavok

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.fabricmc.loader.api.FabricLoader
import online.slavok.config.ConfigManager
import org.slf4j.LoggerFactory

object SimplePlayerHeads : ModInitializer {
	val logger = LoggerFactory.getLogger("simple-player-heads")
	val configManager = ConfigManager(FabricLoader.getInstance().configDir.resolve("simple-player-heads.toml").toFile())

	override fun onInitialize() {
	}
}