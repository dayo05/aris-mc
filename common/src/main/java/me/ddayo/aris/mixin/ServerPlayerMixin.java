package me.ddayo.aris.mixin;

import me.ddayo.aris.engine.hook.EntityHooks;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    /**
     * Direct drop (e.g. from code, creative mode)
     */
    @Inject(method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("HEAD"), cancellable = true)
    private void onDropItem(ItemStack itemStack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir) {
        if (itemStack.isEmpty()) return;
        ServerPlayer self = (ServerPlayer) (Object) this;
        if (EntityHooks.INSTANCE.executeOnItemDrop(self, itemStack)) {
            self.getInventory().add(itemStack);
            self.inventoryMenu.sendAllDataToRemote();
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void onAttack(Entity target, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        ServerPlayer self = (ServerPlayer) (Object) this;
        if (EntityHooks.INSTANCE.executeOnEntityAttack(self, target)) {
            ci.cancel();
        }
    }

    @Inject(method = "die", at = @At("HEAD"))
    private void onDie(DamageSource source, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        EntityHooks.INSTANCE.executeOnPlayerDeath((ServerPlayer) (Object) this);
    }

    @Inject(method = "restoreFrom", at = @At("TAIL"))
    private void onRestoreFrom(ServerPlayer oldPlayer, boolean keepEverything, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        EntityHooks.INSTANCE.executeOnPlayerRespawn((ServerPlayer) (Object) this);
    }

    @Inject(method = "completeUsingItem", at = @At("HEAD"))
    private void onCompleteUsingItem(org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        ServerPlayer self = (ServerPlayer) (Object) this;
        EntityHooks.INSTANCE.executeOnItemConsume(self, self.getUseItem());
    }
}
