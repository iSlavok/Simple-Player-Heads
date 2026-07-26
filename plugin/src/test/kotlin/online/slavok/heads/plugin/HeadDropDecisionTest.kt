package online.slavok.heads.plugin

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class HeadDropDecisionTest {

    @Test
    fun `self kill follows selfKill flag`() {
        assertTrue(HeadDropDecision.shouldDrop(HeadConfig(selfKill = true), killerIsPlayer = true, killerIsSelf = true))
        assertFalse(HeadDropDecision.shouldDrop(HeadConfig(selfKill = false), killerIsPlayer = true, killerIsSelf = true))
    }

    @Test
    fun `player kill follows playerKill flag`() {
        assertTrue(HeadDropDecision.shouldDrop(HeadConfig(playerKill = true), killerIsPlayer = true, killerIsSelf = false))
        assertFalse(HeadDropDecision.shouldDrop(HeadConfig(playerKill = false), killerIsPlayer = true, killerIsSelf = false))
    }

    @Test
    fun `other death follows otherDeaths flag`() {
        assertTrue(HeadDropDecision.shouldDrop(HeadConfig(otherDeaths = true), killerIsPlayer = false, killerIsSelf = false))
        assertFalse(HeadDropDecision.shouldDrop(HeadConfig(otherDeaths = false), killerIsPlayer = false, killerIsSelf = false))
    }

    @Test
    fun `self kill flag is independent of the other flags`() {
        val config = HeadConfig(selfKill = true, playerKill = false, otherDeaths = false)
        assertTrue(HeadDropDecision.shouldDrop(config, killerIsPlayer = true, killerIsSelf = true))
        assertFalse(HeadDropDecision.shouldDrop(config, killerIsPlayer = true, killerIsSelf = false))
        assertFalse(HeadDropDecision.shouldDrop(config, killerIsPlayer = false, killerIsSelf = false))
    }
}
