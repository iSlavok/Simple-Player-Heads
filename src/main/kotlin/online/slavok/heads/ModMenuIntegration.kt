package online.slavok.heads

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import me.shedaniel.clothconfig2.api.ConfigBuilder
import online.slavok.heads.config.Config
//? if >=1.22 {
/*import net.minecraft.network.chat.Component
*///?} else {
import net.minecraft.text.Text
//?}
//? if <1.19 {
/*import net.minecraft.text.LiteralText*/
//?}

/**
 * A tiny Cloth Config screen exposed through ModMenu. ModMenu is optional; when it is
 * absent this entrypoint is simply never called and the config stays file-only.
 */
class ModMenuIntegration : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> = ConfigScreenFactory { parent ->
        val config = SimplePlayerHeads.configManager.config
        val builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(literal("Simple Player Heads"))
        val category = builder.getOrCreateCategory(literal("General"))
        val entries = builder.entryBuilder()

        var selfKill = config.selfKill
        var playerKill = config.playerKill
        var otherDeaths = config.otherDeaths

        category.addEntry(
            entries.startBooleanToggle(literal("selfKill"), config.selfKill)
                .setDefaultValue(true)
                .setSaveConsumer { selfKill = it }
                .build(),
        )
        category.addEntry(
            entries.startBooleanToggle(literal("playerKill"), config.playerKill)
                .setDefaultValue(true)
                .setSaveConsumer { playerKill = it }
                .build(),
        )
        category.addEntry(
            entries.startBooleanToggle(literal("otherDeaths"), config.otherDeaths)
                .setDefaultValue(true)
                .setSaveConsumer { otherDeaths = it }
                .build(),
        )

        builder.setSavingRunnable {
            SimplePlayerHeads.configManager.save(Config(selfKill, playerKill, otherDeaths))
        }
        builder.build()
    }
}

//? if >=1.22 {
/*private fun literal(text: String): Component = Component.literal(text)*/
//?} elif >=1.19 {
private fun literal(text: String): Text = Text.literal(text)
//?} else {
/*private fun literal(text: String): Text = LiteralText(text)*/
//?}
