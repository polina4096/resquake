package polina4096.resquake

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import dev.isxander.yacl.api.ConfigCategory
import dev.isxander.yacl.api.Option
import dev.isxander.yacl.api.YetAnotherConfigLib
import dev.isxander.yacl.gui.controllers.BooleanController
import dev.isxander.yacl.gui.controllers.string.number.DoubleFieldController
import net.minecraft.text.Text

class ModMenuIntegration : ModMenuApi {
    override fun getModConfigScreenFactory() = ConfigScreenFactory {
        YetAnotherConfigLib.createBuilder()
            .title(Text.of(ReSquakeMod.NAME))
            .category(ConfigCategory.createBuilder()
                .name(Text.of("Movement"))
                .tooltip(Text.of("Changes made by this mod"))

                .option(Option.createBuilder(Boolean::class.javaPrimitiveType)
                    .name(Text.of("Bunnyhop"))
                    .tooltip(Text.of("Enables/disables all movement changes made by this mod"))
                    .binding(ReSquakeConfig.DEFAULT_BUNNYHOP_ENABLED,
                        { ReSquakeMod.config.bunnyhopEnabled },
                        { ReSquakeMod.config.bunnyhopEnabled = it })
                    .controller(::BooleanController)
                    .build())

                .option(Option.createBuilder(Boolean::class.javaPrimitiveType)
                    .name(Text.of("Uncapped bunnyhop"))
                    .tooltip(Text.of("If enabled, the soft and hard speed caps will not be applied at all"))
                    .binding(ReSquakeConfig.DEFAULT_UNCAPPED_BUNNYHOP,
                        { ReSquakeMod.config.uncappedBunnyhop },
                        { ReSquakeMod.config.uncappedBunnyhop = it })
                    .controller(::BooleanController)
                    .build())

                .option(Option.createBuilder(Boolean::class.javaPrimitiveType)
                    .name(Text.of("Trimping"))
                    .tooltip(Text.of("Enables/disables trimping (big jump when sneaking)"))
                    .binding(ReSquakeConfig.DEFAULT_TRIMPING_ENABLED,
                        { ReSquakeMod.config.trimpingEnabled },
                        { ReSquakeMod.config.trimpingEnabled = it })
                    .controller(::BooleanController)
                    .build())

                .option(Option.createBuilder(Boolean::class.javaPrimitiveType)
                    .name(Text.of("No jump cooldown"))
                    .tooltip(Text.of("Enables/disables jump cooldown (better to turn on)"))
                    .binding(ReSquakeConfig.DEFAULT_TRIMPING_ENABLED,
                        { ReSquakeMod.config.noJumpCooldown },
                        { ReSquakeMod.config.noJumpCooldown = it })
                    .controller(::BooleanController)
                    .build())

                .build())

            .category(ConfigCategory.createBuilder()
                .name(Text.of("Constants"))
                .tooltip(Text.of("Movement-related values"))

                .option(Option.createBuilder(Double::class.javaPrimitiveType)
                    .name(Text.of("Soft cap threshold"))
                    .tooltip(Text.of("soft cap speed = (moveSpeed*softCapThreshold)"))
                    .binding(ReSquakeConfig.DEFAULT_SOFT_CAP_THRESHOLD,
                        { ReSquakeMod.config.softCapThreshold },
                        { ReSquakeMod.config.softCapThreshold = it })
                    .controller(::DoubleFieldController)
                    .build())

                .option(Option.createBuilder(Double::class.javaPrimitiveType)
                    .name(Text.of("Hard cap threshold"))
                    .tooltip(Text.of("If you jump while above the hard cap speed (moveSpeed*hardCapThreshold), your speed is set to the hard cap speed"))
                    .binding(ReSquakeConfig.DEFAULT_HARD_CAP_THRESHOLD,
                        { ReSquakeMod.config.hardCapThreshold },
                        { ReSquakeMod.config.hardCapThreshold = it })
                    .controller(::DoubleFieldController)
                    .build())

                .option(Option.createBuilder(Double::class.javaPrimitiveType)
                    .name(Text.of("Acceleration"))
                    .tooltip(Text.of("A higher value means you accelerate faster on the ground"))
                    .binding(ReSquakeConfig.DEFAULT_ACCELERATION,
                        { ReSquakeMod.config.acceleration },
                        { ReSquakeMod.config.acceleration = it })
                    .controller(::DoubleFieldController)
                    .build())

                .option(Option.createBuilder(Double::class.javaPrimitiveType)
                    .name(Text.of("Air acceleration"))
                    .tooltip(Text.of("A higher value means you can turn more sharply in the air without losing speed"))
                    .binding(ReSquakeConfig.DEFAULT_AIR_ACCELERATION,
                        { ReSquakeMod.config.airAcceleration },
                        { ReSquakeMod.config.airAcceleration = it })
                    .controller(::DoubleFieldController)
                    .build())

                .option(Option.createBuilder(Double::class.javaPrimitiveType)
                    .name(Text.of("Max air acceleration per tick"))
                    .tooltip(Text.of("Limit for how much you can accelerate in a tick"))
                    .binding(ReSquakeConfig.DEFAULT_MAX_AACEL_PER_TICK,
                        { ReSquakeMod.config.maxAAccPerTick },
                        { ReSquakeMod.config.maxAAccPerTick = it })
                    .controller(::DoubleFieldController)
                    .build())

                .option(Option.createBuilder(Double::class.javaPrimitiveType)
                    .name(Text.of("Soft cap degen"))
                    .tooltip(Text.of("The modifier used to calculate speed lost when jumping above the soft cap"))
                    .binding(ReSquakeConfig.DEFAULT_SOFT_CAP_DEGEN,
                        { ReSquakeMod.config.softCapDegen },
                        { ReSquakeMod.config.softCapDegen = it })
                    .controller(::DoubleFieldController)
                    .build())

                .option(Option.createBuilder(Double::class.javaPrimitiveType)
                    .name(Text.of("Trimp multiplier"))
                    .tooltip(Text.of("A lower value means less horizontal speed converted to vertical speed"))
                    .binding(ReSquakeConfig.DEFAULT_TRIMP_MULTIPLIER,
                        { ReSquakeMod.config.trimpMultiplier },
                        { ReSquakeMod.config.trimpMultiplier = it })
                    .controller(::DoubleFieldController)
                    .build())

                .build())
            .save(ReSquakeMod.config::save)
            .build()
            .generateScreen(it)
    }
}