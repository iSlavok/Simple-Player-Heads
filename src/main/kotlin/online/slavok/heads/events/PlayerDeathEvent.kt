package online.slavok.heads.events

import com.mojang.authlib.GameProfile
import online.slavok.heads.ModBehavior
import online.slavok.heads.SimplePlayerHeads
//? if <1.22 {
import net.minecraft.entity.damage.DamageSource
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
//?} else {
/*import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items*/
//?}
//? if <1.20.5 {
/*import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtHelper
*///?} elif <1.22 {
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.ProfileComponent
//?} else {
/*import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.component.ResolvableProfile
*///?}

// Player and inventory classes were renamed in the unobfuscated (26+) mappings.
//? if <1.22 {
private typealias PlayerInv = net.minecraft.entity.player.PlayerInventory
private typealias PlayerType = net.minecraft.entity.player.PlayerEntity
//?} else {
/*private typealias PlayerInv = net.minecraft.world.entity.player.Inventory
private typealias PlayerType = net.minecraft.world.entity.player.Player*/
//?}

object PlayerDeathEvent {
    @JvmStatic
    fun onPlayerDeath(gameProfile: GameProfile, inventory: PlayerInv, damageSource: DamageSource) {
        if (!ModBehavior.active) return
        if (shouldDropHead(gameProfile, damageSource)) {
            addHead(gameProfile, inventory)
        }
    }

    private fun shouldDropHead(gameProfile: GameProfile, damageSource: DamageSource): Boolean {
        val config = SimplePlayerHeads.configManager.config
        //? if >=1.22 {
        /*val attacker = damageSource.entity*/
        //?} else {
        val attacker = damageSource.attacker
        //?}
        return when {
            attacker is PlayerType && attacker.gameProfile == gameProfile -> config.selfKill
            attacker is PlayerType -> config.playerKill
            else -> config.otherDeaths
        }
    }

    private fun addHead(gameProfile: GameProfile, inventory: PlayerInv) {
        val head = ItemStack(Items.PLAYER_HEAD)
        //? if <1.20.5 {
        /*val skullOwner = NbtHelper.writeGameProfile(NbtCompound(), gameProfile)
        head.orCreateNbt.put("SkullOwner", skullOwner)
        *///?} elif <1.22 {
        head.set(DataComponentTypes.PROFILE, ProfileComponent(gameProfile))
        //?} else {
        /*head.set(DataComponents.PROFILE, ResolvableProfile.createResolved(gameProfile))
        *///?}
        //? if >=1.22 {
        /*inventory.add(head)*/
        //?} else {
        inventory.insertStack(head)
        //?}
    }
}
