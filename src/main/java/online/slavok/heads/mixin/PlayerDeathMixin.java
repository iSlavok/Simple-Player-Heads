package online.slavok.heads.mixin;

//? if >=1.22 {
/*import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
*///?} else {
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.damage.DamageSource;
//?}
import online.slavok.heads.events.PlayerDeathEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >=1.22 {
/*@Mixin(ServerPlayer.class)
*///?} else {
@Mixin(ServerPlayerEntity.class)
//?}
public class PlayerDeathMixin {
	//? if >=1.22 {
	/*@Inject(at = @At("HEAD"), method = "die")
	private void simplePlayerHeads$onDeath(DamageSource damageSource, CallbackInfo info) {
		ServerPlayer self = (ServerPlayer) (Object) this;
		PlayerDeathEvent.onPlayerDeath(self.getGameProfile(), self.getInventory(), damageSource);
	}
	*///?} else {
	@Inject(at = @At("HEAD"), method = "onDeath")
	private void simplePlayerHeads$onDeath(DamageSource damageSource, CallbackInfo info) {
		ServerPlayerEntity self = (ServerPlayerEntity) (Object) this;
		PlayerDeathEvent.onPlayerDeath(self.getGameProfile(), self.getInventory(), damageSource);
	}
	//?}
}
