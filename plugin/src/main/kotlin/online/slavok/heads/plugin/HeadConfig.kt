package online.slavok.heads.plugin

/** Per-Looting-level drop chance for kills by another player. Mirrors the mod's config section. */
data class PlayerKillLooting(
    val enabled: Boolean = false,
    val noLooting: Double = 1.0,
    val looting1: Double = 1.0,
    val looting2: Double = 1.0,
    val looting3: Double = 1.0,
)

/** The gameplay flags, mirrored from the mod's config. */
data class HeadConfig(
    val selfKill: Boolean = true,
    val playerKill: Boolean = true,
    val otherDeaths: Boolean = true,
    val playerKillLooting: PlayerKillLooting = PlayerKillLooting(),
)
