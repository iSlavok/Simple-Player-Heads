package online.slavok.heads.plugin

/**
 * Pure decision: given the config, how the victim died, and the killer's Looting level,
 * what is the head's drop probability [0.0, 1.0]? Mirrors the mod's dropChance.
 * `killerIsSelf` and `lootingLevel` are only meaningful when `killerIsPlayer`.
 */
object HeadDropDecision {
    fun dropChance(
        config: HeadConfig,
        killerIsPlayer: Boolean,
        killerIsSelf: Boolean,
        lootingLevel: Int,
    ): Double = when {
        killerIsPlayer && killerIsSelf -> if (config.selfKill) 1.0 else 0.0
        killerIsPlayer -> playerKillChance(config, lootingLevel)
        else -> if (config.otherDeaths) 1.0 else 0.0
    }

    private fun playerKillChance(config: HeadConfig, lootingLevel: Int): Double {
        if (!config.playerKill) return 0.0
        val looting = config.playerKillLooting
        if (!looting.enabled) return 1.0
        return when (lootingLevel.coerceIn(0, 3)) {
            0 -> looting.noLooting
            1 -> looting.looting1
            2 -> looting.looting2
            else -> looting.looting3
        }
    }
}
