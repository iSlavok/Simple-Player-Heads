package online.slavok.heads.events

import com.mojang.authlib.GameProfile
import online.slavok.heads.ModBehavior
import online.slavok.heads.SimplePlayerHeads
import online.slavok.heads.config.Config
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
// Looting enchantment access: static object <1.20.5, registry entry 1.20.5+, Mojang names 1.22+.
//? if <1.22 {
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
//?} else {
/*import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.core.registries.Registries*/
//?}
//? if >=1.21 && <1.22 {
import net.minecraft.registry.RegistryKeys
//?}

// Player, inventory and world classes were renamed in the unobfuscated (26+) mappings.
//? if <1.22 {
private typealias PlayerInv = net.minecraft.entity.player.PlayerInventory
private typealias PlayerType = net.minecraft.entity.player.PlayerEntity
private typealias WorldType = net.minecraft.world.World
//?} else {
/*private typealias PlayerInv = net.minecraft.world.entity.player.Inventory
private typealias PlayerType = net.minecraft.world.entity.player.Player
private typealias WorldType = net.minecraft.world.level.Level*/
//?}

object PlayerDeathEvent {
    @JvmStatic
    fun onPlayerDeath(gameProfile: GameProfile, inventory: PlayerInv, damageSource: DamageSource) {
        if (!ModBehavior.active) return
        //? if >=1.22 {
        /*val attacker = damageSource.entity*/
        //?} else {
        val attacker = damageSource.attacker
        //?}
        val killerIsPlayer = attacker is PlayerType
        val killerIsSelf = attacker is PlayerType && attacker.gameProfile == gameProfile
        val lootingLevel = if (attacker is PlayerType) lootingLevelOf(attacker) else 0
        val chance = dropChance(
            SimplePlayerHeads.configManager.config, killerIsPlayer, killerIsSelf, lootingLevel,
        )
        if (rolls(chance, attacker)) addHead(gameProfile, inventory)
    }

    /** Looting level of the killer's main-hand weapon (mappings rename the accessors at 1.22). */
    private fun lootingLevelOf(attacker: PlayerType): Int {
        //? if <1.22 {
        val weapon = attacker.mainHandStack
        val world = attacker.world
        //?} else {
        /*val weapon = attacker.mainHandItem
        val world = attacker.level()*/
        //?}
        return lootingLevel(weapon, world)
    }

    /**
     * Whether the head drops for [chance]. A fractional chance only arises on the player-kill path
     * (looting enabled), where [attacker] is a player and supplies the RNG; 1.0/0.0 short-circuit.
     */
    private fun rolls(chance: Double, attacker: Any?): Boolean = when {
        chance <= 0.0 -> false
        chance >= 1.0 -> true
        attacker is PlayerType -> attacker.random.nextDouble() < chance
        else -> false
    }

    /**
     * Pure decision mirrored from the plugin's HeadDropDecision: the head's drop probability
     * [0.0, 1.0] given how the victim died and the killer's Looting level. `killerIsSelf` and
     * `lootingLevel` are only meaningful when `killerIsPlayer`. Public so gametests can assert it.
     */
    @JvmStatic
    fun dropChance(
        config: Config,
        killerIsPlayer: Boolean,
        killerIsSelf: Boolean,
        lootingLevel: Int,
    ): Double = when {
        killerIsPlayer && killerIsSelf -> if (config.selfKill) 1.0 else 0.0
        killerIsPlayer -> playerKillChance(config, lootingLevel)
        else -> if (config.otherDeaths) 1.0 else 0.0
    }

    private fun playerKillChance(config: Config, lootingLevel: Int): Double {
        if (!config.playerKill) return 0.0
        val looting = config.playerKillLooting
        if (!looting.enabled) return 1.0
        return when (lootingLevel.coerceIn(0, 3)) {
            0 -> looting.noLooting
            1 -> looting.looting1
            2 -> looting.looting2
            else -> looting.looting3
        }
    }

    /** Looting level of [weapon]; 0 if unenchanted. [world] supplies the enchantment registry (1.20.5+). */
    @JvmStatic
    fun lootingLevel(weapon: ItemStack, world: WorldType): Int {
        // <1.21: Enchantments.LOOTING is the Enchantment value. 1.21+: it is a RegistryKey, so
        // resolve it to a RegistryEntry via the world registry. 1.22+: Mojang names + Holder.
        //? if <1.21 {
        /*return EnchantmentHelper.getLevel(Enchantments.LOOTING, weapon)
        *///?} elif <1.22 {
        val entry = world.registryManager.getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.LOOTING)
        return EnchantmentHelper.getLevel(entry, weapon)
        //?} else {
        /*val holder = world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.LOOTING)
        return EnchantmentHelper.getItemEnchantmentLevel(holder, weapon)
        *///?}
    }

    private fun addHead(gameProfile: GameProfile, inventory: PlayerInv) {
        val head = createHead(gameProfile)
        //? if >=1.22 {
        /*inventory.add(head)*/
        //?} else {
        inventory.insertStack(head)
        //?}
    }

    /** Builds a player head carrying [gameProfile]. Public so gametests can verify it per version. */
    @JvmStatic
    fun createHead(gameProfile: GameProfile): ItemStack {
        val head = ItemStack(Items.PLAYER_HEAD)
        //? if <1.20.5 {
        /*val skullOwner = NbtHelper.writeGameProfile(NbtCompound(), gameProfile)
        head.orCreateNbt.put("SkullOwner", skullOwner)
        *///?} elif <1.22 {
        head.set(DataComponentTypes.PROFILE, ProfileComponent(gameProfile))
        //?} else {
        /*head.set(DataComponents.PROFILE, ResolvableProfile.createResolved(gameProfile))
        *///?}
        return head
    }
}
