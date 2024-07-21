package online.slavok.heads.config

import kotlinx.serialization.Serializable
import net.peanuuutz.tomlkt.TomlComment

@Serializable
data class Config(
    @TomlComment("If true, player will get his head after self kill")
    val selfKill: Boolean = true,
    @TomlComment("If true, player will get his head after kill by another player")
    val playerKill: Boolean = true,
    @TomlComment("If true, player will get his head after all other deaths")
    val otherDeaths: Boolean = true
)
