package polina4096.resquake

import net.minecraft.block.BlockRenderType
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.Flutterer
import net.minecraft.entity.MovementType
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.BlockStateParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.util.math.Vec3d
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.sqrt

object ReSquakeClient {
    private var baseVelocities = mutableListOf<Pair<Double, Double>>()

    // API
    fun updateVelocity(player: Entity, speed: Float, sidemove: Double, forwardmove: Double): Boolean {
        // When the bunnyhop is enabled
        if (!ReSquakeMod.config.bunnyhopEnabled) return false

        if (player !is PlayerEntity) return false // We are only interested in players
        if (!player.world.isClient)  return false // And only in the client player

        // And only on land
        if((player.abilities.flying && player.vehicle == null) || player.isTouchingWater || player.isInLava || player.isClimbing)
            return false

        val wishspeed = speed.toDouble() * 2.15
        val wishdir = player.getMovementDirection(sidemove, forwardmove)
        val wishvel = Pair(wishdir.first * wishspeed, wishdir.second * wishspeed)
        baseVelocities.add(wishvel)

        return true
    }
    fun afterJump(player: PlayerEntity) {
        if (player.world.isClient && ReSquakeMod.config.bunnyhopEnabled) {
            if (player.isSprinting) {
                val f = player.yaw * 0.017453292f
                val xVel = player.velocity.x + sin(f) * 0.2
                val zVel = player.velocity.z - cos(f) * 0.2
                player.velocity = Vec3d(xVel, player.velocity.y, zVel)
            }

            val maxMoveSpeed = player.getBaseSpeedMax()

            player.applySoftCap(maxMoveSpeed)

            val didTrimp = player.trimp()
            if (!didTrimp) player.applyHardCap(maxMoveSpeed)
            player.spawnBunnyhopParticles(ReSquakeMod.config.bunnyhopParticles)
        }
    }
    fun travel(player: PlayerEntity, sidemove: Double, forwardmove: Double): Boolean {
        if (!ReSquakeMod.config.bunnyhopEnabled                                       ) return false
        if (!player.world.isClient                                                    ) return false
        if ((player.abilities.flying || player.isFallFlying) && player.vehicle == null) return false

        val dX = player.x
        val dY = player.y
        val dZ = player.z
        if (player.travelQuake(sidemove, forwardmove)) {
            player.increaseTravelMotionStats(player.x - dX, player.y - dY, player.z - dZ)

            return true
        }

        return false
    }
    fun beforeTick(player: PlayerEntity) {
        if (player.world.isClient && baseVelocities.isNotEmpty())
            baseVelocities.clear()
    }

    // Quake movement
    private fun PlayerEntity.getMovementDirection(sidemoveInitial: Double, forwardmoveInitial: Double): Pair<Double, Double> {
        val preSpeed = sidemoveInitial * sidemoveInitial + forwardmoveInitial * forwardmoveInitial
        if (preSpeed >= 0) {
            var speed = sqrt(preSpeed)

            speed = if (speed < 1.0) 1.0
                    else             1.0 / speed

            val sidemove    = sidemoveInitial    * speed
            val forwardmove = forwardmoveInitial * speed
            val f1 = sin(this.yaw * Math.PI / 180.0)
            val f2 = cos(this.yaw * Math.PI / 180.0)

            return Pair(
                (sidemove    * f2 - forwardmove * f1),
                (forwardmove * f2 + sidemove    * f1),
            )
        }

        return Pair(0.0, 0.0)
    }

    private const val QUAKE_MOVEMENT_SPEED_MULTIPLIER = 2.15
    private const val QUAKE_SNEAKING_SPEED_MULTIPLIER = 1.10
    private fun PlayerEntity.getSlipperiness(): Double {
        if (this.isOnGround) {
            val groundPos = BlockPos.ofFloored(this.x, this.boundingBox.minY - 1, this.z)
            return this.world.getBlockState(groundPos).block.slipperiness.toDouble()
        }

        return 0.0
    }
    private fun PlayerEntity.getBaseSpeedCurrent(): Double {
        val baseSpeed = this.movementSpeed
        return if (!this.isSneaking) baseSpeed * QUAKE_MOVEMENT_SPEED_MULTIPLIER else baseSpeed * QUAKE_SNEAKING_SPEED_MULTIPLIER
    }
    private fun PlayerEntity.getBaseSpeedMax(): Double {
        val baseSpeed = this.movementSpeed
        return baseSpeed * QUAKE_MOVEMENT_SPEED_MULTIPLIER
    }
    private fun PlayerEntity.getSpeed(): Double {
        val x = this.velocity.x
        val z = this.velocity.z
        return sqrt((x * x + z * z))
    }

    private fun PlayerEntity.travelQuake(sidemove: Double, forwardmove: Double): Boolean {
        // take care of ladder movement using default code
        if (this.isClimbing                               ) return false
        if (this.isInLava && !this.abilities.flying       ) return false
        if (this.isTouchingWater && !this.abilities.flying) return false // TODO: sharking

        // get all relevant movement values
        val wishdir = this.getMovementDirection(sidemove, forwardmove)
        val wishspeed = if (sidemove != 0.0 || forwardmove != 0.0) this.getBaseSpeedCurrent() else 0.0
        val onGroundForReal = this.isOnGround && !MinecraftClient.getInstance().player!!.input.jumping

        // ground movement
        if (onGroundForReal) {
            val slipperiness = this.getSlipperiness()
            val xVel = this.velocity.x * slipperiness
            val zVel = this.velocity.z * slipperiness
            this.velocity = Vec3d(xVel, this.velocity.y, zVel)

            if (wishspeed != 0.0) {
                // alter based on the surface friction
                val acceleration = ReSquakeMod.config.acceleration * (0.16277136 / (slipperiness * slipperiness * slipperiness))
                this.accelerate(wishspeed, wishdir.first, wishdir.second, acceleration, slipperiness)
            }

            if (baseVelocities.isNotEmpty()) {
                var x = this.velocity.x
                var z = this.velocity.z
                val speedMod = wishspeed / this.getBaseSpeedMax()

                // add in base velocities
                for (baseVel in baseVelocities) {
                    x += baseVel.first  * speedMod
                    z += baseVel.second * speedMod
                }

                this.velocity = Vec3d(x, this.velocity.y, z)
            }
        } else { // air movement
            val airAcceleration = ReSquakeMod.config.airAcceleration
            this.airAccelerate(wishspeed, wishdir.first, wishdir.second, airAcceleration)

            // TODO: sharking here
            // some code must be here :')
        }

        // apply velocity
        this.move(MovementType.SELF, this.velocity)

        // HL2 code applies half gravity before acceleration and half after acceleration, but this seems to work fine
        this.applyGravity()

        // Move arms and legs
        this.updateLimbs(this is Flutterer)

        return true
    }

    private fun PlayerEntity.applyGravity() {
        val levitating = hasStatusEffect(StatusEffects.LEVITATION)

        var yVel = this.velocity.y
        var gravity = -0.08 // gravity

        // Slow falling
        if (velocity.y <= 0.0 && hasStatusEffect(StatusEffects.SLOW_FALLING)) {
            gravity = -0.01
            this.onLanding()
        }

        // Levitation
        if (levitating) {
            yVel += (0.05 * (getStatusEffect(StatusEffects.LEVITATION)!!.amplifier + 1).toDouble() - yVel) * 0.2
            onLanding()
        }

        // Apply gravity
        if (!world.isClient || world.chunkManager.isChunkLoaded(ChunkSectionPos.getSectionCoord(blockPos.x), ChunkSectionPos.getSectionCoord(blockPos.z))) {
            if (!hasNoGravity() && !levitating)
                yVel += gravity

            val airResistance = 0.9800000190734863
            this.velocity = Vec3d(this.velocity.x, yVel * airResistance, this.velocity.z)
        }

        else { // If chunk is not loaded slowly fall to bottomY
            yVel = if (this.y > world.bottomY.toDouble()) -0.1 else 0.0
            this.velocity = Vec3d(this.velocity.x, yVel, this.velocity.z)
        }

    }
    private fun PlayerEntity.accelerate(wishspeed: Double, wishX: Double, wishZ: Double, acceleration: Double, slipperiness: Double) {
        // Determine veer amount; this is a dot product
        val currentSpeed = this.velocity.x * wishX + this.velocity.z * wishZ

        // Speed delta
        val addSpeed = wishspeed - currentSpeed

        // If not adding any, done
        if (addSpeed <= 0) return

        // Determine acceleration speed after acceleration
        var accelSpeed = acceleration * wishspeed / slipperiness * 0.05
        if (accelSpeed > addSpeed) accelSpeed = addSpeed

        // Adjust move velocity
        val x = this.velocity.x + accelSpeed * wishX
        val z = this.velocity.z + accelSpeed * wishZ
        this.velocity = Vec3d(x, this.velocity.y, z)
    }
    private fun PlayerEntity.airAccelerate(wishspeedInitial: Double, wishX: Double, wishZ: Double, accel: Double) {
        val maxAirAcceleration = ReSquakeMod.config.maxAAccPerTick
        val wishspeed = if (wishspeedInitial > maxAirAcceleration) maxAirAcceleration else wishspeedInitial

        // Determine veer amount; this is a dot product
        val currentSpeed = this.velocity.x * wishX + this.velocity.z * wishZ

        // Speed delta
        val addSpeed = wishspeed - currentSpeed

        // If not adding any, done
        if (addSpeed <= 0) return

        // Determine acceleration speed after acceleration
        var accelSpeed = accel * wishspeedInitial * 0.05
        if (accelSpeed > addSpeed) accelSpeed = addSpeed

        // Adjust move velocity
        val x = this.velocity.x + accelSpeed * wishX
        val z = this.velocity.z + accelSpeed * wishZ
        this.velocity = Vec3d(x, this.velocity.y, z)
    }
    private fun PlayerEntity.applySoftCap(moveSpeed: Double) {
        var softCapPercent = ReSquakeMod.config.softCapThreshold
        var softCapDegen   = ReSquakeMod.config.softCapDegen
        if (ReSquakeMod.config.bunnyhopUncapped) {
            softCapPercent = 1.0
            softCapDegen   = 1.0
        }

        val speed = this.getSpeed()
        val softCap = moveSpeed * softCapPercent

        // apply soft cap first; if soft -> hard is not done, then you can continually trigger only the hard cap and stay at the hard cap
        if (speed > softCap) {
            if (softCapDegen != 1.0) {
                val appliedCap = (speed - softCap) * softCapDegen + softCap
                val multiplier = appliedCap / speed

                val x = this.velocity.x * multiplier
                val z = this.velocity.z * multiplier
                this.velocity = Vec3d(x, this.velocity.y, z)
            }
        }
    }
    private fun PlayerEntity.applyHardCap(moveSpeed: Double) {
        if (ReSquakeMod.config.bunnyhopUncapped) return

        val hardCapPercent = ReSquakeMod.config.hardCapThreshold
        val hardCap = moveSpeed * hardCapPercent
        val speed = this.getSpeed()

        if (speed > hardCap && hardCap != 0.0) {
            val multiplier = hardCap / speed
            val xVel = this.velocity.x * multiplier
            val zVel = this.velocity.z * multiplier
            this.velocity = Vec3d(xVel, this.velocity.y, zVel)
        }
    }

    // Trimping
    private fun PlayerEntity.trimp(): Boolean {
        if (ReSquakeMod.config.trimpingEnabled && this.isSneaking) {
            val currentSpeed     = this.getSpeed()
            val maxMovementSpeed = this.getBaseSpeedMax()

            // Player has gained at least some additional speed to walking speed
            if (currentSpeed > maxMovementSpeed) {
                var speedBonus = currentSpeed / maxMovementSpeed * 0.5
                if (speedBonus > 1.0) speedBonus = 1.0

                val trimpMultiplier = ReSquakeMod.config.trimpMultiplier

                if (trimpMultiplier > 0) {
                    val multiplier = 1.0 / trimpMultiplier
                    val xVel = this.velocity.x * multiplier
                    val zVel = this.velocity.z * multiplier
                    val yVel = speedBonus * currentSpeed * trimpMultiplier
                    this.velocity = Vec3d(xVel, yVel, zVel)
                }

                this.spawnBunnyhopParticles(ReSquakeMod.config.bunnyhopParticles * 2)

                return true
            }
        }

        return false
    }

    // Particles
    private fun PlayerEntity.spawnBunnyhopParticles(numParticles: Int) {
        if (numParticles < 1) return

        // taken from sprint
        val i = floor(this.x                      ).toInt()
        val j = floor(this.y - 0.20000000298023224).toInt()
        val k = floor(this.z                      ).toInt()

        val blockState = this.world.getBlockState(BlockPos(i, j, k))
        if (blockState.renderType != BlockRenderType.INVISIBLE) {
            for (iParticle in 0 until numParticles) {
                val x = this.x + (this.random.nextFloat() - 0.5) * this.width
                val z = this.z + (this.random.nextFloat() - 0.5) * this.width
                val y = this.boundingBox.minY + 0.1

                val xVel = -this.velocity.x * 4.0
                val zVel = -this.velocity.z
                val yVel = 1.5

                val effect = BlockStateParticleEffect(ParticleTypes.BLOCK, blockState)
                this.world.addParticle(effect, x, y, z, xVel, yVel, zVel)
            }
        }
    }
}