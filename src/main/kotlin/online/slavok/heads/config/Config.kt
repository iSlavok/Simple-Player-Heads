package online.slavok.heads.config

import kotlinx.serialization.Serializable
import net.peanuuutz.tomlkt.TomlComment

@Serializable
data class PlayerKillLooting(
    @TomlComment("Enable per-Looting-level drop chance for kills by another player")
    val enabled: Boolean = false,
    @TomlComment("Drop chance (0.0-1.0) when the killer's weapon has no Looting")
    val noLooting: Double = 0.0,
    @TomlComment("Drop chance with Looting I")
    val looting1: Double = 0.33,
    @TomlComment("Drop chance with Looting II")
    val looting2: Double = 0.67,
    @TomlComment("Drop chance with Looting III (and above)")
    val looting3: Double = 1.0,
)

@Serializable
data class Config(
    @TomlComment("If true, player will get his head after self kill")
    val selfKill: Boolean = true,
    @TomlComment("If true, player will get his head after kill by another player")
    val playerKill: Boolean = true,
    @TomlComment("If true, player will get his head after all other deaths")
    val otherDeaths: Boolean = true,
    @TomlComment("Only used when playerKill is true; playerKill=false never drops on a player kill")
    val playerKillLooting: PlayerKillLooting = PlayerKillLooting()
)
