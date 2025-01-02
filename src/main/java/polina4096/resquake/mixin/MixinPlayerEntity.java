package polina4096.resquake.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import polina4096.resquake.ReSquakePlayer;

@SuppressWarnings("unused")
@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity {
  protected MixinPlayerEntity(EntityType<? extends LivingEntity> entityType, World world) {
    super(entityType, world);
  }

  @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
  public void travelInject(Vec3d movementInput, CallbackInfo ci) {
    PlayerEntity player = (PlayerEntity) (Object) this;
    if (ReSquakePlayer.INSTANCE.travel(player, movementInput)) {
      ci.cancel();
    }
  }

  @Inject(method = "tick", at = @At("HEAD"))
  public void tickInject(CallbackInfo ci) {
    PlayerEntity player = (PlayerEntity) (Object) this;
    ReSquakePlayer.INSTANCE.beforeTick(player);
  }

  public boolean velocityChanged = false;

  @Inject(at = @At("HEAD"), method = "handleFallDamage")
  private void beforeFall(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
    // Store velocity changed state before fall damage is applied
    if (!this.getWorld().isClient)
      velocityChanged = this.velocityModified;
  }

  @Inject(method = "handleFallDamage", at = @At("RETURN"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;increaseStat(Lnet/minecraft/util/Identifier;I)V"), to = @At("TAIL")))
  private void afterFall(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
    // Cancel fall damage velocity by restoring state to original one before fall damage was applied if the player is bunnyhopping
    if (!this.getWorld().isClient)
      this.velocityModified = velocityChanged;
  }
}
