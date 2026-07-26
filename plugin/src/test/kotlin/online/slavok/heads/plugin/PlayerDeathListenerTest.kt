package online.slavok.heads.plugin

import org.bukkit.Material
import org.bukkit.damage.DamageSource
import org.bukkit.damage.DamageType
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockbukkit.mockbukkit.MockBukkit
import org.mockbukkit.mockbukkit.ServerMock
import org.mockbukkit.mockbukkit.entity.PlayerMock
import java.util.Random

class PlayerDeathListenerTest {
    private lateinit var server: ServerMock

    @BeforeEach fun setUp() { server = MockBukkit.mock() }
    @AfterEach fun tearDown() { MockBukkit.unmock() }

    /** Builds a PlayerDeathEvent for `victim`, with `killer` as its killer, no keepInventory. */
    private fun deathEvent(victim: PlayerMock, killer: Player?): PlayerDeathEvent {
        victim.killer = killer
        val source = DamageSource.builder(DamageType.GENERIC).build()
        return PlayerDeathEvent(victim, source, mutableListOf<ItemStack>(), 0, victim.name)
    }

    private fun heads(drops: List<ItemStack>) = drops.count { it.type == Material.PLAYER_HEAD }

    @Test
    fun `looting-II weapon uses looting2 bucket and rolls a drop`() {
        val config = HeadConfig(
            playerKill = true,
            playerKillLooting = PlayerKillLooting(enabled = true, noLooting = 0.0, looting2 = 0.5),
        )
        // random.nextDouble() = 0.4 < 0.5 -> drop
        val listener = PlayerDeathListener({ config }, seededDouble(0.4))
        val victim = server.addPlayer()
        val killer = server.addPlayer()
        val sword = ItemStack(Material.DIAMOND_SWORD).apply { addUnsafeEnchantment(Enchantment.LOOTING, 2) }
        killer.inventory.setItemInMainHand(sword)

        val event = deathEvent(victim, killer)
        listener.onDeath(event)

        assertEquals(1, heads(event.drops))
    }

    @Test
    fun `looting-II weapon skips drop when roll exceeds chance`() {
        val config = HeadConfig(
            playerKill = true,
            playerKillLooting = PlayerKillLooting(enabled = true, noLooting = 0.0, looting2 = 0.5),
        )
        // random.nextDouble() = 0.6 !< 0.5 -> no drop
        val listener = PlayerDeathListener({ config }, seededDouble(0.6))
        val victim = server.addPlayer()
        val killer = server.addPlayer()
        val sword = ItemStack(Material.DIAMOND_SWORD).apply { addUnsafeEnchantment(Enchantment.LOOTING, 2) }
        killer.inventory.setItemInMainHand(sword)

        val event = deathEvent(victim, killer)
        listener.onDeath(event)

        assertEquals(0, heads(event.drops))
    }

    @Test
    fun `no looting with noLooting zero drops nothing`() {
        val config = HeadConfig(
            playerKill = true,
            playerKillLooting = PlayerKillLooting(enabled = true, noLooting = 0.0),
        )
        val listener = PlayerDeathListener({ config }, seededDouble(0.0))
        val victim = server.addPlayer()
        val killer = server.addPlayer()
        killer.inventory.setItemInMainHand(ItemStack(Material.STICK))

        val event = deathEvent(victim, killer)
        listener.onDeath(event)

        assertEquals(0, heads(event.drops))
    }

    @Test
    fun `default config still drops on every player kill`() {
        val listener = PlayerDeathListener({ HeadConfig() }, seededDouble(0.99))
        val victim = server.addPlayer()
        val killer = server.addPlayer()
        killer.inventory.setItemInMainHand(ItemStack(Material.STICK))

        val event = deathEvent(victim, killer)
        listener.onDeath(event)

        assertEquals(1, heads(event.drops))
    }

    /** A Random whose nextDouble() always returns `value`. */
    private fun seededDouble(value: Double) = object : Random() {
        override fun nextDouble() = value
    }
}
