package online.slavok.heads.events

import com.mojang.authlib.GameProfile
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import online.slavok.heads.ModBehavior
import online.slavok.heads.SimplePlayerHeads
//? if <1.20.5 {
/*import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtHelper
*///?} else {
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.ProfileComponent
//?}

object PlayerDeathEvent {
    @JvmStatic
    fun onPlayerDeath(gameProfile: GameProfile, inventory: PlayerInventory, damageSource: DamageSource) {
        if (!ModBehavior.active) return
        if (shouldDropHead(gameProfile, damageSource)) {
            addHead(gameProfile, inventory)
        }
    }

    private fun shouldDropHead(gameProfile: GameProfile, damageSource: DamageSource): Boolean {
        val config = SimplePlayerHeads.configManager.config
        val attacker = damageSource.attacker
        return when {
            attacker is PlayerEntity && attacker.gameProfile == gameProfile -> config.selfKill
            attacker is PlayerEntity -> config.playerKill
            else -> config.otherDeaths
        }
    }

    private fun addHead(gameProfile: GameProfile, inventory: PlayerInventory) {
        val head: ItemStack = Items.PLAYER_HEAD.defaultStack
        //? if <1.20.5 {
        /*val skullOwner = NbtHelper.writeGameProfile(NbtCompound(), gameProfile)
        head.orCreateNbt.put("SkullOwner", skullOwner)
        *///?} else {
        head.set(DataComponentTypes.PROFILE, ProfileComponent(gameProfile))
        //?}
        inventory.insertStack(head)
    }
}
