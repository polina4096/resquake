package polina4096.resquake

import net.minecraft.stat.StatFormatter
import net.minecraft.stat.Stats
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object ReSquakeStats {
  val BHOP_ONE_CM = Identifier.of(ReSquakeMod.ID, "bhop_one_cm")!!
  val SHARK_ONE_CM = Identifier.of(ReSquakeMod.ID, "shark_one_cm")!!
  val TRIMPS = Identifier.of(ReSquakeMod.ID, "trimps")!!

  fun register() {
    registerStat(BHOP_ONE_CM, "bhop_one_cm", StatFormatter.DISTANCE)
    registerStat(SHARK_ONE_CM, "shark_one_cm", StatFormatter.DISTANCE)
    registerStat(TRIMPS, "trimps", StatFormatter.DEFAULT)
  }

  private fun registerStat(key: Identifier, id: String, formatter: StatFormatter) {
    Registry.register(Registry.CUSTOM_STAT, id, key)
    Stats.CUSTOM.getOrCreateStat(key, formatter)
  }
}