package polina4096.resquake

import net.fabricmc.api.ModInitializer
import net.minecraft.client.MinecraftClient
import org.slf4j.LoggerFactory
import java.io.File

object ReSquakeMod : ModInitializer {
	const val ID   = "resquake"
	const val NAME = "re:squake"

	val logger = LoggerFactory.getLogger(ID)

	lateinit var config: ReSquakeConfig

	override fun onInitialize() {
		val runDir = MinecraftClient.getInstance().runDirectory
		config = ReSquakeConfig.load(File(runDir, "config").resolve("$ID.json"))

		logger.info("re:squake initialized!")
	}
}