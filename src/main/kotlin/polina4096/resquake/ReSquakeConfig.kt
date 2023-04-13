package polina4096.resquake

import kotlinx.serialization.*
import kotlinx.serialization.Transient
import kotlinx.serialization.json.*
import java.awt.Color
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.writeText

private val json = Json { prettyPrint = true }

@Serializable
class ReSquakeConfig(@Transient var path: Path? = null) {
    /* General */
    // Movement
    var quakeMovementEnabled       : Boolean = DEFAULT_QUAKE_MOVEMENT_ENABLED
    var trimpingEnabled            : Boolean = DEFAULT_TRIMPING_ENABLED

    // Miscellaneous
    var uncappedBunnyhop           : Boolean = DEFAULT_UNCAPPED_BUNNYHOP
    var noJumpCooldown             : Boolean = DEFAULT_NO_JUMP_COOLDOWN
    var jumpParticles              : Int     = DEFAULT_JUMP_PARTICLES

    // Speed indicator
    var speedDeltaIndicatorEnabled : Boolean = DEFAULT_SPEED_DELTA_INDICATOR_ENABLED
    var speedDeltaThreshold        : Double  = DEFAULT_SPEED_DELTA_THRESHOLD
    var speedGainColor             : Int     = SPEED_GAIN_COLOR      . rgb
    var speedLossColor             : Int     = SPEED_LOSS_COLOR      . rgb
    var speedUnchangedColor        : Int     = SPEED_UNCHANGED_COLOR . rgb

    /* Movement constants */
    // Bunnyhop
    var softCapThreshold : Double = DEFAULT_SOFT_CAP_THRESHOLD
    var hardCapThreshold : Double = DEFAULT_HARD_CAP_THRESHOLD
    var acceleration     : Double = DEFAULT_ACCELERATION
    var airAcceleration  : Double = DEFAULT_AIR_ACCELERATION
    var maxAAccPerTick   : Double = DEFAULT_MAX_AACEL_PER_TICK
    var softCapDegen     : Double = DEFAULT_SOFT_CAP_DEGEN

    // Trimping
    var trimpMultiplier  : Double = DEFAULT_TRIMP_MULTIPLIER

    fun save() {
        path!!.writeText(json.encodeToString(this))
    }

    companion object {
        /* General */
        // Movement
        const val DEFAULT_QUAKE_MOVEMENT_ENABLED        = true
        const val DEFAULT_TRIMPING_ENABLED              = true

        // Miscellaneous
        const val DEFAULT_UNCAPPED_BUNNYHOP             = true
        const val DEFAULT_NO_JUMP_COOLDOWN              = true
        const val DEFAULT_JUMP_PARTICLES                = 4

        // Speed indicator
        const val DEFAULT_SPEED_DELTA_INDICATOR_ENABLED = true
        const val DEFAULT_SPEED_DELTA_THRESHOLD         = 6.0
      /* s */ val SPEED_GAIN_COLOR                      = Color(0xFF_00FF00.toInt())
      /* a */ val SPEED_LOSS_COLOR                      = Color(0xFF_FF0000.toInt())
      /* d */ val SPEED_UNCHANGED_COLOR                 = Color(0xFF_FFFFFF.toInt())

        /* Movement constants */
        // Bunnyhop
        const val DEFAULT_SOFT_CAP_THRESHOLD = 544.00
        const val DEFAULT_HARD_CAP_THRESHOLD = 544.00
        const val DEFAULT_ACCELERATION       =  10.00
        const val DEFAULT_AIR_ACCELERATION   =  10.00
        const val DEFAULT_MAX_AACEL_PER_TICK =   0.05
        const val DEFAULT_SOFT_CAP_DEGEN     =   0.65

        // Trimping
        const val DEFAULT_TRIMP_MULTIPLIER   =   1.40

        fun load(path: Path): ReSquakeConfig {
            if (!path.exists()) return ReSquakeConfig(path)

            val inputStream = Files.newInputStream(path)
            val inputString = inputStream.bufferedReader().use { it.readText() }
            return Json.decodeFromString<ReSquakeConfig>(inputString).also { it.path = path }
        }
    }
}
