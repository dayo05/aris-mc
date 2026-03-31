package me.ddayo.aris.mixin;

import me.ddayo.aris.engine.hook.EntityHooks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin {
    @Inject(method = "doClick", at = @At("HEAD"), cancellable = true)
    private void onDoClick(int slotIndex, int button, ClickType clickType, Player player, CallbackInfo ci) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        ItemStack cursorStack = ((AbstractContainerMenu) (Object) this).getCarried();
        ItemStack slotStack = slotIndex >= 0 && slotIndex < ((AbstractContainerMenu) (Object) this).slots.size()
                ? ((AbstractContainerMenu) (Object) this).slots.get(slotIndex).getItem()
                : ItemStack.EMPTY;
        ItemStack targetItem = !cursorStack.isEmpty() ? cursorStack : slotStack;
        if (targetItem.isEmpty()) return;
        if (EntityHooks.INSTANCE.executeOnContainerClick(serverPlayer, targetItem)) {
            ci.cancel();
        }
    }
}
