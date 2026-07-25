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
