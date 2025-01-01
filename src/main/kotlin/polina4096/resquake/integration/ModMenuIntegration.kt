package polina4096.resquake.integration

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import polina4096.resquake.generateConfigScreen

@Environment(EnvType.CLIENT)
class ModMenuIntegration : ModMenuApi {
  override fun getModConfigScreenFactory() = ConfigScreenFactory { generateConfigScreen(it) }
}