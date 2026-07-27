package online.slavok.heads.gametest;

import com.mojang.authlib.GameProfile;
import online.slavok.heads.events.PlayerDeathEvent;

import java.util.UUID;

//? if >=1.21 {
import net.fabricmc.fabric.api.gametest.v1.GameTest;
//?} else {
/*import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;*/
//?}
//? if >=1.22 {
/*import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.component.DataComponents;*/
//?} else {
import net.minecraft.test.TestContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
//?}
//? if >=1.20.5 && <1.22 {
import net.minecraft.component.DataComponentTypes;
//?}
// Looting enchant application for the lootingLevel test (mirrors the version-split enchant API).
//? if <1.22 {
import net.minecraft.server.world.ServerWorld;
import net.minecraft.enchantment.Enchantments;
//?} else {
/*import net.minecraft.world.level.Level;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;*/
//?}
//? if >=1.21 && <1.22 {
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.enchantment.Enchantment;
//?}

/**
 * Verifies that the version-specific head creation actually produces a profile-bearing
 * player head at runtime, on every supported Minecraft version. This is an A/B test: the
 * created head must carry the profile, while a plain player head must not — proving our
 * code (not the item itself) attaches the profile. Booting the gametest server also loads
 * the mixin target class; with a required mixin, a wrong target would crash on load.
 */
//? if >=1.21 {
public class HeadDropGameTest {
//?} else {
/*public class HeadDropGameTest implements FabricGameTest {*/
//?}

    //? if >=1.21 {
    @GameTest
    //?} elif >=1.19 {
    /*@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)*/
    //?} else {
    /*@GameTest(structureName = FabricGameTest.EMPTY_STRUCTURE)*/
    //?}
    //? if >=1.22 {
    /*public void createdHeadCarriesProfile(GameTestHelper context) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), "TestVictim");
        ItemStack head = PlayerDeathEvent.createHead(profile);
        if (head.getItem() != Items.PLAYER_HEAD) throw new RuntimeException("created item is not a player head");
        if (!hasProfile(head)) throw new RuntimeException("created head is missing its profile");
        if (hasProfile(new ItemStack(Items.PLAYER_HEAD))) throw new RuntimeException("control failed: a plain head has a profile");
        context.succeed();
    }*/
    //?} else {
    public void createdHeadCarriesProfile(TestContext context) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), "TestVictim");
        ItemStack head = PlayerDeathEvent.createHead(profile);
        if (head.getItem() != Items.PLAYER_HEAD) throw new RuntimeException("created item is not a player head");
        if (!hasProfile(head)) throw new RuntimeException("created head is missing its profile");
        if (hasProfile(new ItemStack(Items.PLAYER_HEAD))) throw new RuntimeException("control failed: a plain head has a profile");
        context.complete();
    }
    //?}

    //? if >=1.21 {
    @GameTest
    //?} elif >=1.19 {
    /*@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)*/
    //?} else {
    /*@GameTest(structureName = FabricGameTest.EMPTY_STRUCTURE)*/
    //?}
    //? if >=1.22 {
    /*public void dropChanceBucketsByLooting(GameTestHelper context) {
        checkDropChance();
        context.succeed();
    }*/
    //?} else {
    public void dropChanceBucketsByLooting(TestContext context) {
        checkDropChance();
        context.complete();
    }
    //?}

    private static void checkDropChance() {
        online.slavok.heads.config.PlayerKillLooting looting =
                new online.slavok.heads.config.PlayerKillLooting(true, 0.0, 0.25, 0.5, 1.0);
        online.slavok.heads.config.Config config =
                new online.slavok.heads.config.Config(true, true, true, looting);

        // playerKill=false overrides looting.
        online.slavok.heads.config.Config gated =
                new online.slavok.heads.config.Config(true, false, true, looting);
        expect(0.0, PlayerDeathEvent.dropChance(gated, true, false, 3));

        // Buckets by level + clamp above III.
        expect(0.0, PlayerDeathEvent.dropChance(config, true, false, 0));
        expect(0.25, PlayerDeathEvent.dropChance(config, true, false, 1));
        expect(0.5, PlayerDeathEvent.dropChance(config, true, false, 2));
        expect(1.0, PlayerDeathEvent.dropChance(config, true, false, 3));
        expect(1.0, PlayerDeathEvent.dropChance(config, true, false, 7));

        // Self kill / other deaths ignore looting.
        expect(1.0, PlayerDeathEvent.dropChance(config, true, true, 0));
        expect(1.0, PlayerDeathEvent.dropChance(config, false, false, 0));
    }

    private static void expect(double want, double got) {
        if (want != got) throw new RuntimeException("dropChance expected " + want + " got " + got);
    }

    //? if >=1.21 {
    @GameTest
    //?} elif >=1.19 {
    /*@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)*/
    //?} else {
    /*@GameTest(structureName = FabricGameTest.EMPTY_STRUCTURE)*/
    //?}
    //? if >=1.22 {
    /*public void lootingLevelReadsEnchantment(GameTestHelper context) {
        Level world = context.getLevel();
        ItemStack sword = lootingSword(world);
        if (PlayerDeathEvent.lootingLevel(sword, world) != 2)
            throw new RuntimeException("expected Looting 2, got " + PlayerDeathEvent.lootingLevel(sword, world));
        if (PlayerDeathEvent.lootingLevel(new ItemStack(Items.STICK), world) != 0)
            throw new RuntimeException("control failed: a plain item reported Looting");
        context.succeed();
    }*/
    //?} else {
    public void lootingLevelReadsEnchantment(TestContext context) {
        ServerWorld world = context.getWorld();
        ItemStack sword = lootingSword(world);
        if (PlayerDeathEvent.lootingLevel(sword, world) != 2)
            throw new RuntimeException("expected Looting 2, got " + PlayerDeathEvent.lootingLevel(sword, world));
        if (PlayerDeathEvent.lootingLevel(new ItemStack(Items.STICK), world) != 0)
            throw new RuntimeException("control failed: a plain item reported Looting");
        context.complete();
    }
    //?}

    /** Builds a diamond sword enchanted with Looting II, using the version-specific enchant API. */
    private static ItemStack lootingSword(Object rawWorld) {
        //? if >=1.22 {
        /*Level world = (Level) rawWorld;
        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
        Holder<Enchantment> looting = world.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.LOOTING);
        sword.enchant(looting, 2);
        return sword;
        *///?} elif >=1.21 {
        ServerWorld world = (ServerWorld) rawWorld;
        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
        RegistryEntry<Enchantment> looting = world.getRegistryManager()
                .getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.LOOTING);
        sword.addEnchantment(looting, 2);
        return sword;
        //?} else {
        /*ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
        sword.addEnchantment(Enchantments.LOOTING, 2);
        return sword;*/
        //?}
    }

    private static boolean hasProfile(ItemStack head) {
        //? if <1.20.5 {
        /*return head.getNbt() != null && head.getNbt().contains("SkullOwner");*/
        //?} elif <1.22 {
        return head.get(DataComponentTypes.PROFILE) != null;
        //?} else {
        /*return head.get(DataComponents.PROFILE) != null;*/
        //?}
    }
}
