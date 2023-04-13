package polina4096.resquake

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import org.slf4j.LoggerFactory

object ReSquakeMod : ModInitializer {
	const val ID   = "resquake"
	const val NAME = "re:squake"

	val logger = LoggerFactory.getLogger(ID)

	lateinit var config: ReSquakeConfig

	override fun onInitialize() {
		val configDir = FabricLoader.getInstance().configDir
		config = ReSquakeConfig.load(configDir.resolve("$ID.json"))

		val mc = MinecraftClient.getInstance()
		HudRenderCallback.EVENT.register { matrices: MatrixStack, tickDelta: Float ->
			val speed = ReSquakeClient.currentSpeed * 20
			if (!config.speedDeltaIndicatorEnabled || !ReSquakeClient.bunnyHopping || speed < config.speedDeltaThreshold)
				return@register

			val posX = mc.window.scaledWidth  / 2.0f
			val posY = mc.window.scaledHeight / 2.0f
			val text = "%.2f".format(speed)
			val centerOffset = mc.textRenderer.getWidth(text) / 2.0f

			val delta = ReSquakeClient.currentSpeed.compareTo(ReSquakeClient.previousSpeed)
			val color = when {
				delta > 0 -> config.speedGainColor
				delta < 0 -> config.speedLossColor
				     else -> config.speedUnchangedColor
			}

			mc.textRenderer.drawWithShadow(matrices, text, posX - centerOffset, posY + 15, color)
		}

		logger.info("re:squake initialized!")
	}
}