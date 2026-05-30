package me.ddayo.aris.fabric.mixin;

import me.ddayo.aris.engine.hook.EntityHooks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow
    @Final
    private static Logger LOGGER;

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    private final ThreadLocal<EntityHooks.EntityDamageResult> aris$damageResult = new ThreadLocal<>();

    private EntityHooks.EntityDamageResult aris$getDamageResult(DamageSource damageSource, float amount) {
        EntityHooks.EntityDamageResult result = aris$damageResult.get();
        if (result == null) {
            result = EntityHooks.INSTANCE.executeOnEntityGotDamage(damageSource, amount, (LivingEntity) (Object)this);
            aris$damageResult.set(result);
        }
        return result;
    }

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void aris$cancelDamage(ServerLevel w, DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (level().isClientSide) return;
        EntityHooks.EntityDamageResult result = aris$getDamageResult(damageSource, amount);
        if (result.getCancelled()) {
            aris$damageResult.remove();
            cir.setReturnValue(false);
        }
    }

    @ModifyVariable(method = "hurtServer", at = @At("HEAD"), argsOnly = true)
    private float aris$modifyDamageAmount(float amount, ServerLevel w, DamageSource damageSource) {
        if(level().isClientSide) return amount;
        return aris$getDamageResult(damageSource, amount).getAmount();
    }

    @Inject(method = "hurtServer", at = @At("RETURN"))
    private void aris$clearDamageResult(ServerLevel w, DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir) {
        aris$damageResult.remove();
    }
}
