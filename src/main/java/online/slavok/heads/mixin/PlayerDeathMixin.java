package online.slavok.heads.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import online.slavok.heads.events.PlayerDeathEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class PlayerDeathMixin {
	@Inject(at = @At("HEAD"), method = "onDeath")
	private void onDeath(DamageSource damageSource, CallbackInfo info) {
		var instance = ((ServerPlayerEntity)(Object)this);
		new PlayerDeathEvent().onPlayerDeath(instance.getGameProfile(), instance.getInventory(), damageSource);
	}
}