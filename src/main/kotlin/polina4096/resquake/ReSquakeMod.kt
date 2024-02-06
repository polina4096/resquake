package polina4096.resquake

import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory

object ReSquakeMod : ModInitializer {
	const val ID   = "resquake"
	const val NAME = "re:squake"

	val logger = LoggerFactory.getLogger(ID)

	lateinit var config: ReSquakeConfig

	override fun onInitialize() {
		val configDir = FabricLoader.getInstance().configDir
		config = ReSquakeConfig.load(configDir.resolve("$ID.json"))

		ReSquakeStats.register();

		logger.info("re:squake initialized!")
	}
}