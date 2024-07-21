package online.slavok.heads.config

import net.peanuuutz.tomlkt.Toml
import java.io.File

class ConfigManager (
    configFile: File
) {
    var config: Config = Config()

    init {
        if (!configFile.exists()) {
            configFile.createNewFile()
            configFile.writeText(Toml.encodeToString(Config.serializer(), config))
        } else {
            config = Toml.decodeFromString(Config.serializer(), configFile.readText())
        }
    }
}