package online.slavok.heads.plugin

import org.bukkit.plugin.java.JavaPlugin

// `open` so MockBukkit can subclass it (Kotlin classes are final by default).
open class SimplePlayerHeadsPlugin : JavaPlugin() {

    override fun onEnable() {
        saveDefaultConfig()
        server.pluginManager.registerEvents(PlayerDeathListener(::readConfig), this)
    }

    /** Read live, so a config reload/restart is picked up on the next death. */
    private fun readConfig(): HeadConfig {
        val c = config
        return HeadConfig(
            selfKill = c.getBoolean("selfKill", true),
            playerKill = c.getBoolean("playerKill", true),
            otherDeaths = c.getBoolean("otherDeaths", true),
        )
    }
}
