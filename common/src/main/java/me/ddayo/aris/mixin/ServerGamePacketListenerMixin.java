package me.ddayo.aris.mixin;

import me.ddayo.aris.engine.hook.EntityHooks;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerMixin {
    @Shadow
    public ServerPlayer player;

    @Inject(method = "handleAnimate", at = @At("HEAD"))
    private void onHandleAnimate(ServerboundSwingPacket packet, CallbackInfo ci) {
        if (packet.getHand() == InteractionHand.MAIN_HAND) {
            player.server.execute(() -> EntityHooks.INSTANCE.executeOnLeftClick(player));
        }
    }
}
