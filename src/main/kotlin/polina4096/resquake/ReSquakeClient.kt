package polina4096.resquake

import net.minecraft.block.BlockRenderType
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.Flutterer
import net.minecraft.entity.MovementType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.BlockStateParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraft.world.chunk.ChunkStatus

object ReSquakeClient {
    private var baseVelocities = mutableListOf<Vec2f>()

    // Public API
    fun updateVelocity(player: Entity, speed: Float, sidemove: Float, forwardmove: Float): Boolean {
        // When the bunnyhop is enabled
        if (!ReSquakeMod.config.bunnyhopEnabled) return false

        if (player !is PlayerEntity) return false // We are only interested in players
        if (!player.world.isClient)  return false // And only in the client player

        // And only on land
        if(player.isFlying()
        || player.isTouchingWater
        || player.isInLava
        || player.isClimbing)
            return false

        val wishspeed = speed * 2.15f
        val wishdir = player.getMovementDirection(sidemove, forwardmove)
        val wishvel = Vec2f(wishdir.x * wishspeed, wishdir.y * wishspeed)
        baseVelocities.add(wishvel)

        return true
    }
    fun afterJump(player: PlayerEntity) {
        if (player.world.isClient && ReSquakeMod.config.bunnyhopEnabled) {
            if (player.isSprinting) {
                val f = player.yaw * 0.017453292f
                val xVel = player.velocity.x + MathHelper.sin(f) * 0.2f
                val zVel = player.velocity.z - MathHelper.cos(f) * 0.2f
                player.velocity = Vec3d(xVel, player.velocity.y, zVel)
            }

            player.quake_Jump()
        }
    }
    fun moveEntityWithHeading(player: PlayerEntity, sidemove: Float, forwardmove: Float): Boolean {
        if (!ReSquakeMod.config.bunnyhopEnabled) return false
        if (!player.world.isClient) return false

        val dX = player.x
        val dY = player.y
        val dZ = player.z
        val didQuakeMovement = if ((player.abilities.flying || player.isFallFlying) && player.vehicle == null)
            return false else player.quake_moveEntityWithHeading(sidemove, forwardmove)

        if (didQuakeMovement) {
            player.increaseTravelMotionStats(player.x - dX, player.y - dY, player.z - dZ)
        }

        return didQuakeMovement
    }
    fun beforeOnLivingUpdate(player: PlayerEntity) {
        if (player.world.isClient && baseVelocities.isNotEmpty())
            baseVelocities.clear()
    }

    // Implementation
    private fun World.getSlipperiness(pos: BlockPos?): Float {
        return this.getBlockState(pos).block.slipperiness
    }

    private fun PlayerEntity.getSlipperiness(): Float {
        if (this.isOnGround) {
            val x = MathHelper.floor(this.x)
            val y = MathHelper.floor(this.boundingBox.minY) - 1
            val z = MathHelper.floor(this.z)
            val groundPos = BlockPos(x, y, z)

            return 1.0f - this.world.getSlipperiness(groundPos)
        }

        return 1.0f
    }
    private fun PlayerEntity.isFlying(): Boolean = this.abilities.flying && this.vehicle == null
    private fun PlayerEntity.getMovementDirection(sidemoveInitial: Float, forwardmoveInitial: Float): Vec2f {
        var f3 = sidemoveInitial * sidemoveInitial + forwardmoveInitial * forwardmoveInitial

        if (f3 >= 1.0E-4f) {
            f3 = MathHelper.sqrt(f3)

            if (f3 < 1.0f) {
                f3 = 1.0f
            }

            f3 = 1.0f / f3

            val sidemove    = sidemoveInitial    * f3
            val forwardmove = forwardmoveInitial * f3
            val f4 = MathHelper.sin((this.yaw * Math.PI / 180.0f).toFloat())
            val f5 = MathHelper.cos((this.yaw * Math.PI / 180.0f).toFloat())

            return Vec2f(sidemove    * f5 - forwardmove * f4,
                         forwardmove * f5 + sidemove    * f4)
        }

        return Vec2f(0.0f, 0.0f)
    }

    private fun PlayerEntity.minecraft_ApplyFriction(momentumRetention: Float) {
        val x = this.velocity.x * momentumRetention.toDouble()
        val z = this.velocity.z * momentumRetention.toDouble()

        val velocity = this.velocity
        this.velocity = Vec3d(x, velocity.y, z)
    }
    private fun PlayerEntity.minecraft_ApplyGravity() {
        val y = if (this.world.isClient && (!this.world.isChunkLoaded(this.x.toInt(),this.z.toInt()) || this.world.getChunk(BlockPos(this.x.toInt(), this.y.toInt(), this.z.toInt())).status !== ChunkStatus.FULL))
            if (this.y > 0.0) -0.1
            else               0.0
        else this.velocity.y - 0.08 // gravity

        val airResistance = 0.9800000190734863
        this.velocity = Vec3d(this.velocity.x, y * airResistance, this.velocity.z)
    }
    private fun PlayerEntity.minecraft_getMoveSpeed(): Float {
        val f2 = this.getSlipperiness()
        val f3 = 0.16277136f / (f2 * f2 * f2)
        return this.movementSpeed * f3
    }

    private fun PlayerEntity.quake_moveEntityWithHeading(sidemove: Float, forwardmove: Float): Boolean {
        // take care of ladder movement using default code
        if (this.isClimbing) return false
        if (this.isInLava && !this.abilities.flying) return false
        if (this.isTouchingWater && !this.abilities.flying)
            return false // TODO: sharking

        // get all relevant movement values
        val wishdir = this.getMovementDirection(sidemove, forwardmove)
        val wishspeed = if (sidemove != 0.0f || forwardmove != 0.0f) this.quake_getMoveSpeed() else 0.0f
        val onGroundForReal = this.isOnGround && !MinecraftClient.getInstance().player!!.input.jumping
        val momentumRetention = this.getSlipperiness()

        // ground movement
        if (onGroundForReal) {
            // apply friction before acceleration, so we can accelerate back up to max speed afterward
            this.minecraft_ApplyFriction(momentumRetention)
            var svAccelerate = ReSquakeMod.config.acceleration
            if (wishspeed != 0.0f) {
                // alter based on the surface friction
                svAccelerate *= (this.minecraft_getMoveSpeed() * 2.15f / wishspeed).toDouble()
                this.quake_Accelerate(wishspeed, wishdir.x.toDouble(), wishdir.y.toDouble(), svAccelerate)
            }

            if (baseVelocities.isNotEmpty()) {
                var x = this.velocity.x
                var z = this.velocity.z
                val speedMod = wishspeed / this.quake_getMaxMoveSpeed()

                // add in base velocities
                for (baseVel in baseVelocities) {
                    x += (baseVel.x * speedMod).toDouble()
                    z += (baseVel.y * speedMod).toDouble()
                }

                this.velocity = Vec3d(x, this.velocity.y, z)
            }
        } else { // air movement
            val svAirAccelerate = ReSquakeMod.config.airAcceleration
            this.quake_AirAccelerate(wishspeed, wishdir.x.toDouble(), wishdir.y.toDouble(), svAirAccelerate)

            // TODO: sharking here
            // some code must be here :')
        }

        // apply velocity
        this.move(MovementType.SELF, this.velocity)

        // HL2 code applies half gravity before acceleration and half after acceleration, but this seems to work fine
        this.minecraft_ApplyGravity()

        // Move arms and legs
        this.updateLimbs(this is Flutterer);

        return true
    }

    private fun PlayerEntity.quake_getMoveSpeed(): Float {
        val baseSpeed = this.movementSpeed
        return if (!this.isSneaking) baseSpeed * 2.15f else baseSpeed * 1.11f
    }
    private fun PlayerEntity.quake_getMaxMoveSpeed(): Float {
        val baseSpeed = this.movementSpeed
        return baseSpeed * 2.15f
    }
    private fun PlayerEntity.quake_Jump() {
        val maxMoveSpeed = this.quake_getMaxMoveSpeed()

        this.quake_ApplySoftCap(maxMoveSpeed)

        val didTrimp = this.quake_DoTrimp()
        if (!didTrimp) this.quake_ApplyHardCap(maxMoveSpeed)
    }
    private fun PlayerEntity.quake_DoTrimp(): Boolean {
        if (ReSquakeMod.config.trimpingEnabled && this.isSneaking) {
            val curSpeed  = this.getSpeed()
            val moveSpeed = this.quake_getMaxMoveSpeed()
            if (curSpeed > moveSpeed) {
                var speedBonus = curSpeed / moveSpeed * 0.5f
                if (speedBonus > 1.0f) speedBonus = 1.0f

                val trimpMultiplier = ReSquakeMod.config.trimpMultiplier

                if (trimpMultiplier > 0) {
                    val multiplier = 1.0f / trimpMultiplier.toFloat()
                    val xVel = this.velocity.x * multiplier.toDouble()
                    val zVel = this.velocity.z * multiplier.toDouble()
                    val yVel = speedBonus * curSpeed * trimpMultiplier
                    this.velocity = Vec3d(xVel, yVel, zVel)
                }

                this.spawnBunnyhopParticles(30)

                return true
            }
        }

        return false
    }
    private fun PlayerEntity.quake_Accelerate(wishspeed: Float, wishX: Double, wishZ: Double, accel: Double) {
        // Determine veer amount; this is a dot product
        val currentSpeed = this.velocity.x * wishX + this.velocity.z * wishZ

        // See how much to add
        val addSpeed = wishspeed - currentSpeed

        // If not adding any, done.
        if (addSpeed <= 0) return

        // Determine acceleration speed after acceleration
        var accelSpeed = accel * wishspeed / this.getSlipperiness() * 0.05f

        // Cap it
        if (accelSpeed > addSpeed) accelSpeed = addSpeed

        // Adjust move velocity
        val x = this.velocity.x + accelSpeed * wishX
        val z = this.velocity.z + accelSpeed * wishZ
        this.velocity = Vec3d(x, this.velocity.y, z)
    }
    private fun PlayerEntity.quake_AirAccelerate(wishspeedInitial: Float, wishX: Double, wishZ: Double, accel: Double) {
        val maxAirAcceleration = ReSquakeMod.config.maxAAccPerTick.toFloat()
        val wishspeed = if (wishspeedInitial > maxAirAcceleration) maxAirAcceleration else wishspeedInitial

        // Determine veer amount; this is a dot product
        val currentSpeed = this.velocity.x * wishX + this.velocity.z * wishZ

        // See how much to add
        val addSpeed = wishspeed - currentSpeed

        // If not adding any, done.
        if (addSpeed <= 0) return

        // Determine acceleration speed after acceleration
        var accelSpeed = accel * wishspeedInitial * 0.05f

        // Cap it
        if (accelSpeed > addSpeed)
            accelSpeed = addSpeed

        // Adjust move velocity
        val x = this.velocity.x + accelSpeed * wishX
        val z = this.velocity.z + accelSpeed * wishZ
        this.velocity = Vec3d(x, this.velocity.y, z)
    }
    private fun PlayerEntity.quake_ApplySoftCap(moveSpeed: Float) {
        var softCapPercent = ReSquakeMod.config.softCapThreshold.toFloat()
        var softCapDegen   = ReSquakeMod.config.softCapDegen.toFloat()
        if (ReSquakeMod.config.uncappedBunnyhop) {
            softCapPercent = 1.0f
            softCapDegen   = 1.0f
        }

        val speed = this.getSpeed()
        val softCap = moveSpeed * softCapPercent

        // apply soft cap first; if soft -> hard is not done, then you can continually trigger only the hard cap and stay at the hard cap
        if (speed > softCap) {
            if (softCapDegen != 1.0f) {
                val appliedCap = (speed - softCap) * softCapDegen + softCap
                val multiplier = appliedCap / speed

                val x = this.velocity.x * multiplier.toDouble()
                val z = this.velocity.z * multiplier.toDouble()
                this.velocity = Vec3d(x, this.velocity.y, z)
            }

            this.spawnBunnyhopParticles(10)
        }
    }
    private fun PlayerEntity.quake_ApplyHardCap(moveSpeed: Float) {
        if (ReSquakeMod.config.uncappedBunnyhop) return

        val hardCapPercent = ReSquakeMod.config.hardCapThreshold.toFloat()
        val hardCap = moveSpeed * hardCapPercent
        val speed = this.getSpeed()

        if (speed > hardCap && hardCap != 0.0f) {
            val multiplier = hardCap / speed
            val xVel = this.velocity.x * multiplier.toDouble()
            val zVel = this.velocity.z * multiplier.toDouble()
            this.velocity = Vec3d(xVel, this.velocity.y, zVel)

            this.spawnBunnyhopParticles(30)
        }
    }

    private fun PlayerEntity.spawnBunnyhopParticles(numParticles: Int) {
        // taken from sprint
        val i = MathHelper.floor(this.x)
        val j = MathHelper.floor(this.y - 0.20000000298023224)
        val k = MathHelper.floor(this.z)

        val blockState = this.world.getBlockState(BlockPos(i, j, k))
        ReSquakeMod.logger.info(this.y.toString())
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
    private fun PlayerEntity.getSpeed(): Float {
        val x = this.velocity.x
        val z = this.velocity.z
        return MathHelper.sqrt((x * x + z * z).toFloat())
    }
}