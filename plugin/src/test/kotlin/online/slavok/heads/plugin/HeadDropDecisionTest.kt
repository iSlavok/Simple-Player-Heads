package online.slavok.heads.plugin

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class HeadDropDecisionTest {

    private fun chance(config: HeadConfig, player: Boolean, self: Boolean, looting: Int = 0) =
        HeadDropDecision.dropChance(config, player, self, looting)

    @Test
    fun `self kill follows selfKill flag`() {
        assertEquals(1.0, chance(HeadConfig(selfKill = true), player = true, self = true))
        assertEquals(0.0, chance(HeadConfig(selfKill = false), player = true, self = true))
    }

    @Test
    fun `other death follows otherDeaths flag`() {
        assertEquals(1.0, chance(HeadConfig(otherDeaths = true), player = false, self = false))
        assertEquals(0.0, chance(HeadConfig(otherDeaths = false), player = false, self = false))
    }

    @Test
    fun `player kill with looting disabled follows playerKill flag`() {
        assertEquals(1.0, chance(HeadConfig(playerKill = true), player = true, self = false))
        assertEquals(0.0, chance(HeadConfig(playerKill = false), player = true, self = false))
    }

    @Test
    fun `playerKill false overrides looting even when enabled`() {
        val config = HeadConfig(
            playerKill = false,
            playerKillLooting = PlayerKillLooting(enabled = true, noLooting = 1.0, looting3 = 1.0),
        )
        assertEquals(0.0, chance(config, player = true, self = false, looting = 3))
    }

    @Test
    fun `enabled looting selects the bucket by level`() {
        val config = HeadConfig(
            playerKill = true,
            playerKillLooting = PlayerKillLooting(
                enabled = true, noLooting = 0.0, looting1 = 0.25, looting2 = 0.5, looting3 = 1.0,
            ),
        )
        assertEquals(0.0, chance(config, player = true, self = false, looting = 0))
        assertEquals(0.25, chance(config, player = true, self = false, looting = 1))
        assertEquals(0.5, chance(config, player = true, self = false, looting = 2))
        assertEquals(1.0, chance(config, player = true, self = false, looting = 3))
    }

    @Test
    fun `looting above three clamps to looting3`() {
        val config = HeadConfig(
            playerKill = true,
            playerKillLooting = PlayerKillLooting(enabled = true, looting3 = 0.7),
        )
        assertEquals(0.7, chance(config, player = true, self = false, looting = 5))
    }

    @Test
    fun `enabled looting does not affect self kill or other deaths`() {
        val config = HeadConfig(
            selfKill = true, otherDeaths = true,
            playerKillLooting = PlayerKillLooting(enabled = true, noLooting = 0.0),
        )
        assertEquals(1.0, chance(config, player = true, self = true, looting = 0))
        assertEquals(1.0, chance(config, player = false, self = false, looting = 0))
    }
}
