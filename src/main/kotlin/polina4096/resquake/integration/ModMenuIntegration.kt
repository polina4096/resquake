package polina4096.resquake.integration

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack

@Environment(EnvType.CLIENT)
class ModMenuIntegration : ModMenuApi {
  override fun getModConfigScreenFactory(): ConfigScreenFactory<*> = ConfigScreenFactory {
    object : Screen(it.title) {
      override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        this.renderBackground(matrices)

        val tr = MinecraftClient.getInstance().textRenderer
        val text = listOf(
          "Unfortunately, a pretty graphical",
          "configuration screen is not supported for this version.",
          "Please configure the mod through the config file.",
          "",
          "You can reload the config file using the reload key (default: unset)."
        )

        val offset = tr.fontHeight + 2
        val base = text.size * offset / 2
        for ((i, line) in text.withIndex()) {
          val x = (width / 2 - tr.getWidth(line) / 2).toFloat()
          val y = (height / 2 - base + i * offset).toFloat()
          tr.drawWithShadow(matrices, line, x, y, 0xFFFFFF)
        }
      }
    }
  }
}