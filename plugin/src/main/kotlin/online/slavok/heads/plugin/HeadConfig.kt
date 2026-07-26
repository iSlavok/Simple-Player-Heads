package online.slavok.heads.plugin

/** The three gameplay flags, mirrored from the mod's config. */
data class HeadConfig(
    val selfKill: Boolean = true,
    val playerKill: Boolean = true,
    val otherDeaths: Boolean = true,
)
