package me.ddayo.aris.fabric.mixin;

import me.ddayo.aris.engine.hook.EntityHooks;
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
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow
    @Final
    private static Logger LOGGER;

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true)
    public float f(float amount, DamageSource damageSource) {
        if(level().isClientSide) return amount;
        else return EntityHooks.INSTANCE.executeOnEntityGotDamage(damageSource, amount, (LivingEntity) (Object)this);
    }
}
