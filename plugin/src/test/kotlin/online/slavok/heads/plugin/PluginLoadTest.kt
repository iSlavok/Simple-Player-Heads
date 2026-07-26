package online.slavok.heads.plugin

import org.bukkit.event.entity.PlayerDeathEvent
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockbukkit.mockbukkit.MockBukkit
import org.mockbukkit.mockbukkit.ServerMock

class PluginLoadTest {
    private lateinit var server: ServerMock
    private lateinit var plugin: SimplePlayerHeadsPlugin

    @BeforeEach
    fun setUp() {
        server = MockBukkit.mock()
        plugin = MockBukkit.load(SimplePlayerHeadsPlugin::class.java)
    }

    @AfterEach
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    fun `plugin enables`() {
        assertTrue(plugin.isEnabled)
    }

    @Test
    fun `registers a player death listener`() {
        val registered = PlayerDeathEvent.getHandlerList().registeredListeners
        assertTrue(registered.any { it.plugin === plugin })
    }
}
