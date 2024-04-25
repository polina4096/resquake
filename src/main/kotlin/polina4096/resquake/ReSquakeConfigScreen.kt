package polina4096.resquake

import dev.isxander.yacl3.api.*
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder
import dev.isxander.yacl3.api.controller.ColorControllerBuilder
import dev.isxander.yacl3.api.controller.DoubleFieldControllerBuilder
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder
import net.minecraft.text.Text
import net.minecraft.client.gui.screen.Screen
import java.awt.Color

fun generateConfigScreen(parent: Screen?): Screen
    = YetAnotherConfigLib.createBuilder()
        .save(ReSquakeMod.config::save)
        .title(Text.of(ReSquakeMod.NAME))
        .category(
            ConfigCategory.createBuilder()
            .name(Text.of("General"))
            .tooltip(Text.of("Changes to minecraft made by this mod"))
            .group(
                OptionGroup.createBuilder()
                .name(Text.of("Movement"))
                .collapsed(false)
                .option(
                    Option.createBuilder<Boolean>()
                    .name(Text.of("Quake-style movement"))
                    .description(OptionDescription.of(Text.of("Enables/disables all movement changes made by this mod")))
                    .binding(ReSquakeConfig.DEFAULT_QUAKE_MOVEMENT_ENABLED,
                        { ReSquakeMod.config.quakeMovementEnabled },
                        { ReSquakeMod.config.quakeMovementEnabled = it })
                    .controller(BooleanControllerBuilder::create)
                    .build())

                .option(
                    Option.createBuilder<Boolean>()
                    .name(Text.of("Trimping"))
                    .description(OptionDescription.of(Text.of("Enables/disables trimping (big jump when sneaking)")))
                    .binding(ReSquakeConfig.DEFAULT_TRIMPING_ENABLED,
                        { ReSquakeMod.config.trimpingEnabled },
                        { ReSquakeMod.config.trimpingEnabled = it })
                    .controller(BooleanControllerBuilder::create)
                    .build())

                .option(
                    Option.createBuilder<Boolean>()
                    .name(Text.of("Sharking"))
                    .description(OptionDescription.of(Text.of("Enables/disables sharking (water glide)")))
                    .binding(ReSquakeConfig.DEFAULT_SHARKING_ENABLED,
                        { ReSquakeMod.config.sharkingEnabled },
                        { ReSquakeMod.config.sharkingEnabled = it })
                    .controller(BooleanControllerBuilder::create)
                    .build())
                .build())

            .group(
                OptionGroup.createBuilder()
                .name(Text.of("Miscellaneous"))
                .collapsed(false)
                .option(
                    Option.createBuilder<Boolean>()
                    .name(Text.of("Uncapped bunnyhop"))
                    .description(OptionDescription.of(Text.of("If enabled, the soft and hard speed caps will not be applied at all")))
                    .binding(ReSquakeConfig.DEFAULT_UNCAPPED_BUNNYHOP,
                        { ReSquakeMod.config.uncappedBunnyhop },
                        { ReSquakeMod.config.uncappedBunnyhop = it })
                    .controller(BooleanControllerBuilder::create)
                    .build())

                .option(
                        Option.createBuilder<Boolean>()
                    .name(Text.of("No jump cooldown"))
                    .description(OptionDescription.of(Text.of("Enables/disables jump cooldown (better to leave enabled)")))
                    .binding(ReSquakeConfig.DEFAULT_TRIMPING_ENABLED,
                        { ReSquakeMod.config.noJumpCooldown },
                        { ReSquakeMod.config.noJumpCooldown = it })
                    .controller(BooleanControllerBuilder::create)
                    .build())

                .option(
                    Option.createBuilder<Int>()
                    .name(Text.of("Jump particles"))
                    .description(OptionDescription.of(Text.of("Amount of particles that spawn when you hit the ground (0 to disable)")))
                    .binding(ReSquakeConfig.DEFAULT_JUMP_PARTICLES,
                        { ReSquakeMod.config.jumpParticles },
                        { ReSquakeMod.config.jumpParticles = it })
                    .controller(IntegerFieldControllerBuilder::create)
                    .build())
                .build())

            .group(
                OptionGroup.createBuilder()
                .name(Text.of("Speed indicator"))
                .collapsed(false)
                .option(
                    Option.createBuilder<Boolean>()
                    .name(Text.of("Delta indicator"))
                    .description(OptionDescription.of(Text.of("Enables/disables the display of change in speed")))
                    .binding(ReSquakeConfig.DEFAULT_SPEED_DELTA_INDICATOR_ENABLED,
                        { ReSquakeMod.config.speedDeltaIndicatorEnabled },
                        { ReSquakeMod.config.speedDeltaIndicatorEnabled = it })
                    .controller(BooleanControllerBuilder::create)
                    .build())

                .option(
                    Option.createBuilder<Boolean>()
                    .name(Text.of("Difference indicator"))
                    .description(OptionDescription.of(Text.of("Enables/disables the display of +/- speed from last hop")))
                    .binding(ReSquakeConfig.DEFAULT_SPEED_DIFF_INDICATOR_ENABLED,
                             { ReSquakeMod.config.speedDiffIndicatorEnabled },
                             { ReSquakeMod.config.speedDiffIndicatorEnabled = it })
                    .controller(BooleanControllerBuilder::create)
                    .build())

                .option(
                    Option.createBuilder<Double>()
                    .name(Text.of("Speed delta threshold"))
                    .description(OptionDescription.of(Text.of("Minimum speed needed for indicator to appear")))
                    .binding(ReSquakeConfig.DEFAULT_SPEED_DELTA_THRESHOLD,
                        { ReSquakeMod.config.speedDeltaThreshold },
                        { ReSquakeMod.config.speedDeltaThreshold = it })
                    .controller(DoubleFieldControllerBuilder::create)
                    .build())

                .option(
                    Option.createBuilder<Color>()
                    .name(Text.of("Speed gain color"))
                    .description(OptionDescription.of(Text.of("Color of speed delta indicator when you gain additional speed")))
                    .binding(ReSquakeConfig.SPEED_GAIN_COLOR,
                        { Color(ReSquakeMod.config.speedGainColor) },
                        { ReSquakeMod.config.speedGainColor = it.rgb })
                    .controller(ColorControllerBuilder::create)
                    .build())

                .option(
                        Option.createBuilder<Color>()
                    .name(Text.of("Speed loss color"))
                    .description(OptionDescription.of(Text.of("Color of speed delta indicator when you lose gained speed")))
                    .binding(ReSquakeConfig.SPEED_LOSS_COLOR,
                        { Color(ReSquakeMod.config.speedLossColor) },
                        { ReSquakeMod.config.speedLossColor = it.rgb })
                    .controller(ColorControllerBuilder::create)
                    .build())

                .option(
                    Option.createBuilder<Color>()
                    .name(Text.of("Speed unchanged color"))
                    .description(OptionDescription.of(Text.of("Color of speed delta indicator when your speed remains the same")))
                    .binding(ReSquakeConfig.SPEED_UNCHANGED_COLOR,
                        { Color(ReSquakeMod.config.speedUnchangedColor) },
                        { ReSquakeMod.config.speedUnchangedColor = it.rgb })
                    .controller(ColorControllerBuilder::create)
                    .build())
                .build())
            .build())

        .category(
            ConfigCategory.createBuilder()
            .name(Text.of("Constants"))
            .tooltip(Text.of("Movement-related values"))
            .group(
                OptionGroup.createBuilder()
                .name(Text.of("Quake-style movement"))
                .collapsed(false)
                .option(
                    Option.createBuilder<Double>()
                    .name(Text.of("Soft cap threshold"))
                    .description(OptionDescription.of(Text.of("soft cap speed = (moveSpeed*softCapThreshold)")))
                    .binding(ReSquakeConfig.DEFAULT_SOFT_CAP_THRESHOLD,
                        { ReSquakeMod.config.softCapThreshold },
                        { ReSquakeMod.config.softCapThreshold = it })
                    .controller(DoubleFieldControllerBuilder::create)
                    .build())

                .option(
                    Option.createBuilder<Double>()
                    .name(Text.of("Hard cap threshold"))
                    .description(OptionDescription.of(Text.of("If you jump while above the hard cap speed (moveSpeed*hardCapThreshold), your speed is set to the hard cap speed")))
                    .binding(ReSquakeConfig.DEFAULT_HARD_CAP_THRESHOLD,
                        { ReSquakeMod.config.hardCapThreshold },
                        { ReSquakeMod.config.hardCapThreshold = it })
                    .controller(DoubleFieldControllerBuilder::create)
                    .build())

                .option(
                    Option.createBuilder<Double>()
                    .name(Text.of("Acceleration"))
                    .description(OptionDescription.of(Text.of("A higher value means you accelerate faster on the ground")))
                    .binding(ReSquakeConfig.DEFAULT_ACCELERATION,
                        { ReSquakeMod.config.acceleration },
                        { ReSquakeMod.config.acceleration = it })
                    .controller(DoubleFieldControllerBuilder::create)
                    .build())

                .option(
                    Option.createBuilder<Double>()
                    .name(Text.of("Air acceleration"))
                    .description(OptionDescription.of(Text.of("A higher value means you can turn more sharply in the air without losing speed")))
                    .binding(ReSquakeConfig.DEFAULT_AIR_ACCELERATION,
                        { ReSquakeMod.config.airAcceleration },
                        { ReSquakeMod.config.airAcceleration = it })
                    .controller(DoubleFieldControllerBuilder::create)
                    .build())

                .option(
                    Option.createBuilder<Double>()
                    .name(Text.of("Max air acceleration per tick"))
                    .description(OptionDescription.of(Text.of("Limit for how much you can accelerate in a tick")))
                    .binding(ReSquakeConfig.DEFAULT_MAX_AACEL_PER_TICK,
                        { ReSquakeMod.config.maxAAccPerTick },
                        { ReSquakeMod.config.maxAAccPerTick = it })
                    .controller(DoubleFieldControllerBuilder::create)
                    .build())

                .option(
                    Option.createBuilder<Double>()
                    .name(Text.of("Soft cap degen"))
                    .description(OptionDescription.of(Text.of("The modifier used to calculate speed lost when jumping above the soft cap")))
                    .binding(ReSquakeConfig.DEFAULT_SOFT_CAP_DEGEN,
                        { ReSquakeMod.config.softCapDegen },
                        { ReSquakeMod.config.softCapDegen = it })
                    .controller(DoubleFieldControllerBuilder::create)
                    .build())
                .build())

            .group(
                OptionGroup.createBuilder()
                .name(Text.of("Trimping"))
                .collapsed(false)
                .option(
                    Option.createBuilder<Double>()
                    .name(Text.of("Trimp multiplier"))
                    .description(OptionDescription.of(Text.of("A lower value means less horizontal speed converted to vertical speed")))
                    .binding(ReSquakeConfig.DEFAULT_TRIMP_MULTIPLIER,
                        { ReSquakeMod.config.trimpMultiplier },
                        { ReSquakeMod.config.trimpMultiplier = it })
                    .controller(DoubleFieldControllerBuilder::create)
                    .build())
                .build())

            .group(
                OptionGroup.createBuilder()
                .name(Text.of("Sharking"))
                .collapsed(false)
                .option(
                    Option.createBuilder<Double>()
                    .name(Text.of("Surface tension"))
                    .binding(ReSquakeConfig.DEFAULT_SHARKING_SURFACE_TENSION,
                        { ReSquakeMod.config.sharkingSurfaceTension },
                        { ReSquakeMod.config.sharkingSurfaceTension = it })
                    .controller(DoubleFieldControllerBuilder::create)
                    .build())

                .option(
                        Option.createBuilder<Double>()
                    .name(Text.of("Sharking friction"))
                    .binding(ReSquakeConfig.DEFAULT_SHARKING_FRICTION,
                        { ReSquakeMod.config.sharkingFriction },
                        { ReSquakeMod.config.sharkingFriction = it })
                    .controller(DoubleFieldControllerBuilder::create)
                    .build())
                .build())
            .build())
        .build()
        .generateScreen(parent)
