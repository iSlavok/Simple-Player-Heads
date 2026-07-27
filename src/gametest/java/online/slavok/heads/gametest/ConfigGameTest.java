package online.slavok.heads.gametest;

import online.slavok.heads.config.ConfigManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

//? if >=1.21 {
import net.fabricmc.fabric.api.gametest.v1.GameTest;
//?} else {
/*import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;*/
//?}
//? if >=1.22 {
/*import net.minecraft.gametest.framework.GameTestHelper;*/
//?} else {
import net.minecraft.test.TestContext;
//?}

/**
 * Proves the config round-trips through tomlkt with the new playerKillLooting section on every
 * supported Minecraft version: constructing a ConfigManager on a fresh file writes the default
 * config, and that TOML must carry the section with backward-compatible defaults (enabled off).
 * Driving it through ConfigManager exercises the real encode path the mod uses at startup.
 */
//? if >=1.21 {
public class ConfigGameTest {
//?} else {
/*public class ConfigGameTest implements FabricGameTest {*/
//?}

    //? if >=1.21 {
    @GameTest
    //?} elif >=1.19 {
    /*@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)*/
    //?} else {
    /*@GameTest(structureName = FabricGameTest.EMPTY_STRUCTURE)*/
    //?}
    //? if >=1.22 {
    /*public void lootingSectionRoundTrips(GameTestHelper context) {
        run();
        context.succeed();
    }*/
    //?} else {
    public void lootingSectionRoundTrips(TestContext context) {
        run();
        context.complete();
    }
    //?}

    private static void run() {
        try {
            File tmp = File.createTempFile("sph-config-test", ".toml");
            // Delete so ConfigManager sees a missing file and writes the defaults itself.
            if (!tmp.delete()) throw new RuntimeException("could not clear temp config file");
            new ConfigManager(tmp);
            String toml = Files.readString(tmp.toPath());
            tmp.delete();

            if (!toml.contains("playerKillLooting"))
                throw new RuntimeException("config TOML is missing the playerKillLooting section:\n" + toml);
            if (!toml.contains("enabled = false"))
                throw new RuntimeException("playerKillLooting.enabled must default to false:\n" + toml);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
