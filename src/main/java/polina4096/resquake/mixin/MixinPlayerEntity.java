package polina4096.resquake.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import polina4096.resquake.ReSquakeClient;
import polina4096.resquake.ReSquakeMod;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity {
	@Inject(method = "jump", at = @At("TAIL"))
	public void jumpInject(CallbackInfo ci) {
		PlayerEntity player = (PlayerEntity)(Object)this;
		ReSquakeClient.INSTANCE.afterJump(player);
	}

	@Inject(method = "travel", at = @At("HEAD"), cancellable = true)
	public void travelInject(Vec3d movementInput, CallbackInfo ci) {
		PlayerEntity player = (PlayerEntity)(Object)this;
		if (ReSquakeClient.INSTANCE.moveEntityWithHeading(player, (float)movementInput.x, (float)movementInput.z)) {
			ci.cancel();
		}
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void tickInject(CallbackInfo ci) {
		PlayerEntity player = (PlayerEntity)(Object)this;
		ReSquakeClient.INSTANCE.beforeOnLivingUpdate(player);
	}

	public boolean velocityChanged = false;

	@Inject(method = "handleFallDamage", at = @At("HEAD"))
	public void handleFallDamageInject(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
		PlayerEntity player = (PlayerEntity)(Object)this;
		if (player.world.isClient) return;
		velocityChanged = player.velocityDirty;
	}

	@Inject(method = "handleFallDamage", at = @At("RETURN"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;increaseStat(Lnet/minecraft/util/Identifier;I)V"), to = @At("TAIL")))
	public void handleFallDamageInjectSlice(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
		PlayerEntity player = (PlayerEntity)(Object)this;
		if (player.world.isClient) return;
		player.velocityDirty = velocityChanged;
	}
}
