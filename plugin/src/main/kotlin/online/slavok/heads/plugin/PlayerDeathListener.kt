package online.slavok.heads.plugin

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

/**
 * Bukkit counterpart of the mod's ServerPlayer.die mixin. `victim.killer` already resolves a
 * projectile to its shooting player (or null), mirroring the mod's DamageSource.getAttacker().
 */
class PlayerDeathListener(private val configProvider: () -> HeadConfig) : Listener {

    // PlayerDeathEvent is not Cancellable, so no ignoreCancelled here.
    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        val victim = event.entity
        val killer: Player? = victim.killer
        val shouldDrop = HeadDropDecision.shouldDrop(
            config = configProvider(),
            killerIsPlayer = killer != null,
            killerIsSelf = killer != null && killer == victim,
        )
        if (!shouldDrop) return

        val head = createHead(victim)
        // Mirror the mod: head joins the death drops by default; with keepInventory it stays on the player.
        if (event.keepInventory) victim.inventory.addItem(head)
        else event.drops.add(head)
    }

    private fun createHead(owner: Player): ItemStack {
        val head = ItemStack(Material.PLAYER_HEAD)
        val meta = head.itemMeta as SkullMeta
        meta.owningPlayer = owner
        head.itemMeta = meta
        return head
    }
}
