package online.slavok.heads.plugin

/**
 * Pure decision: given the config and how the victim died, should a head be produced?
 * Mirrors the mod's shouldDropHead. `killerIsSelf` is only meaningful when `killerIsPlayer`.
 */
object HeadDropDecision {
    fun shouldDrop(config: HeadConfig, killerIsPlayer: Boolean, killerIsSelf: Boolean): Boolean =
        when {
            killerIsPlayer && killerIsSelf -> config.selfKill
            killerIsPlayer -> config.playerKill
            else -> config.otherDeaths
        }
}
