package online.slavok.events

import com.mojang.authlib.GameProfile
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.ProfileComponent
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import online.slavok.SimplePlayerHeads

class PlayerDeathEvent {
    fun onPlayerDeath(gameProfile: GameProfile, inventory: PlayerInventory, damageSource: DamageSource) {
        val config = SimplePlayerHeads.configManager.config
        if (damageSource.attacker is PlayerEntity) {
            if ((damageSource.attacker as PlayerEntity).gameProfile == gameProfile) {
                if (config.selfKill) {
                    addHead(gameProfile, inventory)
                }
            } else if (config.playerKill) {
                addHead(gameProfile, inventory)
            }
        } else if (config.otherDeaths) {
            addHead(gameProfile, inventory)
        }
    }

    private fun addHead(gameProfile: GameProfile, inventory: PlayerInventory) {
        val head: ItemStack = Items.PLAYER_HEAD.defaultStack
        head.set(DataComponentTypes.PROFILE, ProfileComponent(gameProfile))
        inventory.insertStack(head)
    }
}