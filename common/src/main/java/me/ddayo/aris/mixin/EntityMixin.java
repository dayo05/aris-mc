package me.ddayo.aris.mixin;

import me.ddayo.aris.engine.hook.EntityHooks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "kill(Lnet/minecraft/server/level/ServerLevel;)V", at = @At("HEAD"))
    private void onKill(ServerLevel level, CallbackInfo ci) {
        if ((Object) this instanceof ServerPlayer player) {
            EntityHooks.INSTANCE.executeOnPlayerDeath(player);
        }
    }
}
