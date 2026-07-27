package online.slavok.heads

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import net.fabricmc.loader.api.FabricLoader
import online.slavok.heads.config.Config
import online.slavok.heads.config.PlayerKillLooting
//? if >=1.22 {
/*import net.minecraft.network.chat.Component
*///?} else {
import net.minecraft.text.Text
//?}
//? if <1.19 {
/*import net.minecraft.text.LiteralText*/
//?}
// Config screen library: Cloth Config on <1.20 (YACL has no build there), YACL 3.x on >=1.20.
// Both are optional and not bundled — the screen only appears if the user installs the library.
//? if <1.20 {
/*import me.shedaniel.clothconfig2.api.ConfigBuilder
*///?} else {
import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.YetAnotherConfigLib
import dev.isxander.yacl3.api.controller.DoubleFieldControllerBuilder
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder
//?}

/**
 * Exposes the config through ModMenu. ModMenu and the screen library (Cloth Config / YACL) are
 * all optional: if the screen library is not installed, the factory returns no screen instead of
 * crashing, and the mod stays fully usable through its config file.
 */
class ModMenuIntegration : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> = ConfigScreenFactory { parent ->
        val config = SimplePlayerHeads.configManager.config

        var selfKill = config.selfKill
        var playerKill = config.playerKill
        var otherDeaths = config.otherDeaths
        val looting = config.playerKillLooting
        var lootingEnabled = looting.enabled
        var noLooting = looting.noLooting
        var looting1 = looting.looting1
        var looting2 = looting.looting2
        var looting3 = looting.looting3

        //? if <1.20 {
        /*if (!FabricLoader.getInstance().isModLoaded("cloth-config")) return@ConfigScreenFactory null
        val builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(literal("Simple Player Heads"))
        val category = builder.getOrCreateCategory(literal("General"))
        val entries = builder.entryBuilder()

        category.addEntry(
            entries.startBooleanToggle(literal("selfKill"), selfKill)
                .setDefaultValue(true).setSaveConsumer { selfKill = it }.build(),
        )
        category.addEntry(
            entries.startBooleanToggle(literal("playerKill"), playerKill)
                .setDefaultValue(true).setSaveConsumer { playerKill = it }.build(),
        )
        category.addEntry(
            entries.startBooleanToggle(literal("otherDeaths"), otherDeaths)
                .setDefaultValue(true).setSaveConsumer { otherDeaths = it }.build(),
        )
        category.addEntry(
            entries.startBooleanToggle(literal("playerKillLooting.enabled"), lootingEnabled)
                .setDefaultValue(false).setSaveConsumer { lootingEnabled = it }.build(),
        )
        category.addEntry(
            entries.startDoubleField(literal("playerKillLooting.noLooting"), noLooting)
                .setMin(0.0).setMax(1.0).setDefaultValue(0.0).setSaveConsumer { noLooting = it }.build(),
        )
        category.addEntry(
            entries.startDoubleField(literal("playerKillLooting.looting1"), looting1)
                .setMin(0.0).setMax(1.0).setDefaultValue(0.33).setSaveConsumer { looting1 = it }.build(),
        )
        category.addEntry(
            entries.startDoubleField(literal("playerKillLooting.looting2"), looting2)
                .setMin(0.0).setMax(1.0).setDefaultValue(0.67).setSaveConsumer { looting2 = it }.build(),
        )
        category.addEntry(
            entries.startDoubleField(literal("playerKillLooting.looting3"), looting3)
                .setMin(0.0).setMax(1.0).setDefaultValue(1.0).setSaveConsumer { looting3 = it }.build(),
        )

        builder.setSavingRunnable {
            SimplePlayerHeads.configManager.save(
                Config(selfKill, playerKill, otherDeaths, PlayerKillLooting(lootingEnabled, noLooting, looting1, looting2, looting3)),
            )
        }
        builder.build()*/
        //?} else {
        if (!FabricLoader.getInstance().isModLoaded("yet_another_config_lib_v3")) return@ConfigScreenFactory null
        val yacl = YetAnotherConfigLib.createBuilder()
            .title(literal("Simple Player Heads"))
            .category(
                ConfigCategory.createBuilder()
                    .name(literal("General"))
                    .option(
                        Option.createBuilder<Boolean>()
                            .name(literal("selfKill"))
                            .binding(true, { selfKill }, { selfKill = it })
                            .controller(TickBoxControllerBuilder::create)
                            .build(),
                    )
                    .option(
                        Option.createBuilder<Boolean>()
                            .name(literal("playerKill"))
                            .binding(true, { playerKill }, { playerKill = it })
                            .controller(TickBoxControllerBuilder::create)
                            .build(),
                    )
                    .option(
                        Option.createBuilder<Boolean>()
                            .name(literal("otherDeaths"))
                            .binding(true, { otherDeaths }, { otherDeaths = it })
                            .controller(TickBoxControllerBuilder::create)
                            .build(),
                    )
                    .option(
                        Option.createBuilder<Boolean>()
                            .name(literal("playerKillLooting.enabled"))
                            .binding(false, { lootingEnabled }, { lootingEnabled = it })
                            .controller(TickBoxControllerBuilder::create)
                            .build(),
                    )
                    .option(chanceOption("playerKillLooting.noLooting", 0.0, { noLooting }, { noLooting = it }))
                    .option(chanceOption("playerKillLooting.looting1", 0.33, { looting1 }, { looting1 = it }))
                    .option(chanceOption("playerKillLooting.looting2", 0.67, { looting2 }, { looting2 = it }))
                    .option(chanceOption("playerKillLooting.looting3", 1.0, { looting3 }, { looting3 = it }))
                    .build(),
            )
            .save {
                SimplePlayerHeads.configManager.save(
                    Config(selfKill, playerKill, otherDeaths, PlayerKillLooting(lootingEnabled, noLooting, looting1, looting2, looting3)),
                )
            }
            .build()
        yacl.generateScreen(parent)
        //?}
    }

    //? if >=1.20 {
    /** A 0.0–1.0 drop-chance field option. Supplier/Consumer params so lambda literals at the
     *  call site convert to YACL's Java binding SAMs. */
    private fun chanceOption(
        name: String,
        default: Double,
        getter: java.util.function.Supplier<Double>,
        setter: java.util.function.Consumer<Double>,
    ): Option<Double> =
        Option.createBuilder<Double>()
            .name(literal(name))
            .binding(default, getter, setter)
            .controller { DoubleFieldControllerBuilder.create(it).min(0.0).max(1.0) }
            .build()
    //?}
}

//? if >=1.22 {
/*private fun literal(text: String): Component = Component.literal(text)*/
//?} elif >=1.19 {
private fun literal(text: String): Text = Text.literal(text)
//?} else {
/*private fun literal(text: String): Text = LiteralText(text)*/
//?}
