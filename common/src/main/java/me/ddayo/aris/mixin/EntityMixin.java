package me.ddayo.aris.mixin;

import me.ddayo.aris.engine.hook.EntityHooks;
import me.ddayo.aris.engine.hook.EntityDeathTracker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityDeathTracker {
    @Unique
    private boolean aris$deathHooksFired;

    @Override
    public void aris$fireDeathHooks() {
        if (aris$deathHooksFired) return;

        aris$deathHooksFired = true;
        EntityHooks.INSTANCE.executeOnEntityDeath((Entity) (Object) this);
    }

    @Inject(method = "kill(Lnet/minecraft/server/level/ServerLevel;)V", at = @At("HEAD"))
    private void onKill(ServerLevel level, CallbackInfo ci) {
        aris$fireDeathHooks();
    }
}
