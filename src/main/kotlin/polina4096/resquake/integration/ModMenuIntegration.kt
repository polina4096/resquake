package polina4096.resquake.integration

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import dev.isxander.yacl.api.ConfigCategory
import dev.isxander.yacl.api.Option
import dev.isxander.yacl.api.OptionGroup
import dev.isxander.yacl.api.YetAnotherConfigLib
import dev.isxander.yacl.gui.controllers.BooleanController
import dev.isxander.yacl.gui.controllers.ColorController
import dev.isxander.yacl.gui.controllers.string.number.DoubleFieldController
import dev.isxander.yacl.gui.controllers.string.number.IntegerFieldController
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.text.Text
import polina4096.resquake.ReSquakeConfig
import polina4096.resquake.ReSquakeMod
import java.awt.Color

@Environment(EnvType.CLIENT)
class ModMenuIntegration : ModMenuApi {
    override fun getModConfigScreenFactory() = ConfigScreenFactory {
        YetAnotherConfigLib.createBuilder()
            .save(ReSquakeMod.config::save)
            .title(Text.of(ReSquakeMod.NAME))
            .category(ConfigCategory.createBuilder()
                .name(Text.of("General"))
                .tooltip(Text.of("Changes to minecraft made by this mod"))
                .group(OptionGroup.createBuilder()
                    .name(Text.of("Movement"))
                    .collapsed(false)
                    .option(Option.createBuilder(Boolean::class.javaPrimitiveType)
                        .name(Text.of("Quake-style movement"))
                        .tooltip(Text.of("Enables/disables all movement changes made by this mod"))
                        .binding(ReSquakeConfig.DEFAULT_QUAKE_MOVEMENT_ENABLED,
                            { ReSquakeMod.config.quakeMovementEnabled },
                            { ReSquakeMod.config.quakeMovementEnabled = it })
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
                    .build())

                .option(Option.createBuilder(Boolean::class.javaPrimitiveType)
                    .name(Text.of("Sharking"))
                    .tooltip(Text.of("Enables/disables sharking (water glide)"))
                    .binding(ReSquakeConfig.DEFAULT_SHARKING_ENABLED,
                        { ReSquakeMod.config.sharkingEnabled },
                        { ReSquakeMod.config.sharkingEnabled = it })
                    .controller(::BooleanController)
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Text.of("Miscellaneous"))
                    .collapsed(false)
                    .option(Option.createBuilder(Boolean::class.javaPrimitiveType)
                        .name(Text.of("Uncapped bunnyhop"))
                        .tooltip(Text.of("If enabled, the soft and hard speed caps will not be applied at all"))
                        .binding(ReSquakeConfig.DEFAULT_UNCAPPED_BUNNYHOP,
                            { ReSquakeMod.config.uncappedBunnyhop },
                            { ReSquakeMod.config.uncappedBunnyhop = it })
                        .controller(::BooleanController)
                        .build())

                    .option(Option.createBuilder(Boolean::class.javaPrimitiveType)
                        .name(Text.of("No jump cooldown"))
                        .tooltip(Text.of("Enables/disables jump cooldown (better to leave enabled)"))
                        .binding(ReSquakeConfig.DEFAULT_TRIMPING_ENABLED,
                            { ReSquakeMod.config.noJumpCooldown },
                            { ReSquakeMod.config.noJumpCooldown = it })
                        .controller(::BooleanController)
                        .build())

                    .option(Option.createBuilder(Int::class.javaPrimitiveType)
                        .name(Text.of("Jump particles"))
                        .tooltip(Text.of("Amount of particles that spawn when you hit the ground (0 to disable)"))
                        .binding(ReSquakeConfig.DEFAULT_JUMP_PARTICLES,
                            { ReSquakeMod.config.jumpParticles },
                            { ReSquakeMod.config.jumpParticles = it })
                        .controller(::IntegerFieldController)
                        .build())
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Text.of("Speed indicator"))
                    .collapsed(false)
                    .option(Option.createBuilder(Boolean::class.javaPrimitiveType)
                        .name(Text.of("Delta indicator"))
                        .tooltip(Text.of("Enables/disables the display of change in speed"))
                        .binding(ReSquakeConfig.DEFAULT_SPEED_DELTA_INDICATOR_ENABLED,
                            { ReSquakeMod.config.speedDeltaIndicatorEnabled },
                            { ReSquakeMod.config.speedDeltaIndicatorEnabled = it })
                        .controller(::BooleanController)
                        .build())

                    .option(Option.createBuilder(Double::class.javaPrimitiveType)
                        .name(Text.of("Speed delta threshold"))
                        .tooltip(Text.of("Minimum speed needed for indicator to appear"))
                        .binding(ReSquakeConfig.DEFAULT_SPEED_DELTA_THRESHOLD,
                            { ReSquakeMod.config.speedDeltaThreshold },
                            { ReSquakeMod.config.speedDeltaThreshold = it })
                        .controller(::DoubleFieldController)
                        .build())

                    .option(Option.createBuilder(Color::class.javaPrimitiveType)
                        .name(Text.of("Speed gain color"))
                        .tooltip(Text.of("Color of speed delta indicator when you gain additional speed"))
                        .binding(ReSquakeConfig.SPEED_GAIN_COLOR,
                            { Color(ReSquakeMod.config.speedGainColor) },
                            { ReSquakeMod.config.speedGainColor = it.rgb })
                        .controller(::ColorController)
                        .build())

                    .option(Option.createBuilder(Color::class.javaPrimitiveType)
                        .name(Text.of("Speed loss color"))
                        .tooltip(Text.of("Color of speed delta indicator when you lose gained speed"))
                        .binding(ReSquakeConfig.SPEED_LOSS_COLOR,
                            { Color(ReSquakeMod.config.speedLossColor) },
                            { ReSquakeMod.config.speedLossColor = it.rgb })
                        .controller(::ColorController)
                        .build())

                    .option(Option.createBuilder(Color::class.javaPrimitiveType)
                        .name(Text.of("Speed unchanged color"))
                        .tooltip(Text.of("Color of speed delta indicator when your speed remains the same"))
                        .binding(ReSquakeConfig.SPEED_UNCHANGED_COLOR,
                            { Color(ReSquakeMod.config.speedUnchangedColor) },
                            { ReSquakeMod.config.speedUnchangedColor = it.rgb })
                        .controller(::ColorController)
                        .build())
                    .build())
                .build())

            .category(ConfigCategory.createBuilder()
                .name(Text.of("Constants"))
                .tooltip(Text.of("Movement-related values"))
                .group(OptionGroup.createBuilder()
                    .name(Text.of("Quake-style movement"))
                    .collapsed(false)
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
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Text.of("Trimping"))
                    .collapsed(false)
                    .option(Option.createBuilder(Double::class.javaPrimitiveType)
                        .name(Text.of("Trimp multiplier"))
                        .tooltip(Text.of("A lower value means less horizontal speed converted to vertical speed"))
                        .binding(ReSquakeConfig.DEFAULT_TRIMP_MULTIPLIER,
                            { ReSquakeMod.config.trimpMultiplier },
                            { ReSquakeMod.config.trimpMultiplier = it })
                        .controller(::DoubleFieldController)
                        .build())
                    .build())

                .group(OptionGroup.createBuilder()
                    .name(Text.of("Sharking"))
                    .collapsed(false)
                    .option(Option.createBuilder(Double::class.javaPrimitiveType)
                        .name(Text.of("Surface tension"))
                        .binding(ReSquakeConfig.DEFAULT_SHARKING_SURFACE_TENSION,
                            { ReSquakeMod.config.sharkingSurfaceTension },
                            { ReSquakeMod.config.sharkingSurfaceTension = it })
                        .controller(::DoubleFieldController)
                        .build())

                    .option(Option.createBuilder(Double::class.javaPrimitiveType)
                        .name(Text.of("Sharking friction"))
                        .binding(ReSquakeConfig.DEFAULT_SHARKING_FRICTION,
                            { ReSquakeMod.config.sharkingFriction },
                            { ReSquakeMod.config.sharkingFriction = it })
                        .controller(::DoubleFieldController)
                        .build())
                    .build())
                .build())
            .build()
            .generateScreen(it)
    }
}