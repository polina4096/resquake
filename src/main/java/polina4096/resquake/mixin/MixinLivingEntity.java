package polina4096.resquake.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.LivingEntity;
import polina4096.resquake.ReSquakeMod;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {
    @Shadow int jumpingCooldown;

    @Inject(method = "tickMovement", at = @At(value = "HEAD"))
    public void tickMovement(CallbackInfo ci) {
        if (ReSquakeMod.config.getNoJumpCooldown()) {
            jumpingCooldown = 0;
        }
    }
}