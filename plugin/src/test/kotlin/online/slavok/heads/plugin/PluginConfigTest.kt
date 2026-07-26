package online.slavok.heads.plugin

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

class PluginConfigTest {
    @Test
    fun `looting defaults preserve current behavior`() {
        val looting = PlayerKillLooting()
        assertFalse(looting.enabled)
        assertEquals(1.0, looting.noLooting)
        assertEquals(1.0, looting.looting1)
        assertEquals(1.0, looting.looting2)
        assertEquals(1.0, looting.looting3)
    }

    @Test
    fun `head config nests looting with a default`() {
        assertEquals(PlayerKillLooting(), HeadConfig().playerKillLooting)
    }
}
