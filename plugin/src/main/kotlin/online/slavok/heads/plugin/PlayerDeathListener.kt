package online.slavok.heads.plugin

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.Random

/**
 * Bukkit counterpart of the mod's ServerPlayer.die mixin. `victim.killer` already resolves a
 * projectile to its shooting player (or null). The killer's main-hand Looting level drives the
 * per-level drop chance; `random` is injectable so tests are deterministic.
 */
class PlayerDeathListener(
    private val configProvider: () -> HeadConfig,
    private val random: Random = Random(),
) : Listener {

    // PlayerDeathEvent is not Cancellable, so no ignoreCancelled here.
    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        val victim = event.entity
        val killer: Player? = victim.killer
        val lootingLevel = if (killer != null) lootingLevel(killer.inventory.itemInMainHand) else 0
        val chance = HeadDropDecision.dropChance(
            config = configProvider(),
            killerIsPlayer = killer != null,
            killerIsSelf = killer != null && killer == victim,
            lootingLevel = lootingLevel,
        )
        if (!rolls(chance)) return

        val head = createHead(victim)
        // Mirror the mod: head joins the death drops by default; with keepInventory it stays on the player.
        if (event.keepInventory) victim.inventory.addItem(head)
        else event.drops.add(head)
    }

    private fun rolls(chance: Double): Boolean = when {
        chance <= 0.0 -> false
        chance >= 1.0 -> true
        else -> random.nextDouble() < chance
    }

    /** Looting level of [weapon]; 0 for AIR or an un-enchanted item. */
    private fun lootingLevel(weapon: ItemStack): Int =
        weapon.getEnchantmentLevel(Enchantment.LOOTING)

    private fun createHead(owner: Player): ItemStack {
        val head = ItemStack(Material.PLAYER_HEAD)
        val meta = head.itemMeta as SkullMeta
        meta.owningPlayer = owner
        head.itemMeta = meta
        return head
    }
}
