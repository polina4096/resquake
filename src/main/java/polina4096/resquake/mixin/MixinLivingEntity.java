package polina4096.resquake.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import polina4096.resquake.ReSquakeMod;
import polina4096.resquake.ReSquakePlayer;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {
  @Shadow
  private int jumpingCooldown;

  @Inject(method = "jump", at = @At("TAIL"))
  public void jumpInject(CallbackInfo ci) {
    if (((LivingEntity) (Object) this) instanceof PlayerEntity) {
      PlayerEntity player = (PlayerEntity) (Object) this;
      ReSquakePlayer.INSTANCE.afterJump(player);
    }
  }


  @Inject(method = "tickMovement", at = @At(value = "HEAD"))
  public void tickMovement(CallbackInfo ci) {
    if (ReSquakeMod.config.getNoJumpCooldown()) {
      jumpingCooldown = 0;
    }
  }
}