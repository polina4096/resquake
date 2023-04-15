package polina4096.resquake.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import polina4096.resquake.ReSquakePlayer;

@Mixin(Entity.class)
public abstract class MixinEntity {
	@Inject(method = "updateVelocity", at = @At("HEAD"), cancellable = true)
	public void updateVelocityInject(float speed, Vec3d movementInput, CallbackInfo ci) {
		Entity player = (Entity)(Object)this;
		if (ReSquakePlayer.INSTANCE.updateVelocity(player, speed, movementInput.x, movementInput.z)) {
			ci.cancel();
		}
	}
}