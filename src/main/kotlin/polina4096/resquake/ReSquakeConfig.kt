package polina4096.resquake

import kotlinx.serialization.*
import kotlinx.serialization.Transient
import kotlinx.serialization.json.*
import java.io.File

private val json = Json { prettyPrint = true }

@Serializable
class ReSquakeConfig(@Transient var file: File? = null) {
    // Movement
    var bunnyhopEnabled   : Boolean = DEFAULT_BUNNYHOP_ENABLED
    var bunnyhopUncapped  : Boolean = DEFAULT_BUNNYHOP_UNCAPPED
    var bunnyhopParticles : Int     = DEFAULT_BUNNYHOP_PARTICLES

    var trimpingEnabled   : Boolean = DEFAULT_TRIMPING_ENABLED

    var noJumpCooldown    : Boolean = DEFAULT_NO_JUMP_COOLDOWN

    // Constants
    var softCapThreshold  : Double  = DEFAULT_SOFT_CAP_THRESHOLD
    var hardCapThreshold  : Double  = DEFAULT_HARD_CAP_THRESHOLD
    var acceleration      : Double  = DEFAULT_ACCELERATION
    var airAcceleration   : Double  = DEFAULT_AIR_ACCELERATION
    var maxAAccPerTick    : Double  = DEFAULT_MAX_AACEL_PER_TICK
    var softCapDegen      : Double  = DEFAULT_SOFT_CAP_DEGEN
    var trimpMultiplier   : Double  = DEFAULT_TRIMP_MULTIPLIER

    fun save() {
        file!!.writeText(json.encodeToString(this))
    }

    companion object {
        // Movement
        const val DEFAULT_BUNNYHOP_ENABLED    = true
        const val DEFAULT_BUNNYHOP_UNCAPPED   = true
        const val DEFAULT_BUNNYHOP_PARTICLES  =    4

        const val DEFAULT_TRIMPING_ENABLED    = true

        const val DEFAULT_NO_JUMP_COOLDOWN    = true

        // Constants
        const val DEFAULT_SOFT_CAP_THRESHOLD  = 544.00
        const val DEFAULT_HARD_CAP_THRESHOLD  = 544.00
        const val DEFAULT_ACCELERATION        =  10.00
        const val DEFAULT_AIR_ACCELERATION    =  10.00
        const val DEFAULT_MAX_AACEL_PER_TICK  =   0.05
        const val DEFAULT_SOFT_CAP_DEGEN      =   0.65
        const val DEFAULT_TRIMP_MULTIPLIER    =   1.40

        fun load(file: File): ReSquakeConfig {
            if (!file.exists()) return ReSquakeConfig(file)

            val inputStream = file.inputStream()
            val inputString = inputStream.bufferedReader().use { it.readText() }
            return Json.decodeFromString<ReSquakeConfig>(inputString).also { it.file = file }
        }
    }
}
