package online.slavok.heads.plugin

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

class PluginConfigTest {
    @Test
    fun `looting defaults - disabled with a graduated chance curve`() {
        // Disabled by default -> behavior unchanged (chances are ignored until enabled).
        // The chance defaults are a suggested curve for when a server flips enabled=true.
        val looting = PlayerKillLooting()
        assertFalse(looting.enabled)
        assertEquals(0.0, looting.noLooting)
        assertEquals(0.33, looting.looting1)
        assertEquals(0.67, looting.looting2)
        assertEquals(1.0, looting.looting3)
    }

    @Test
    fun `head config nests looting with a default`() {
        assertEquals(PlayerKillLooting(), HeadConfig().playerKillLooting)
    }
}
