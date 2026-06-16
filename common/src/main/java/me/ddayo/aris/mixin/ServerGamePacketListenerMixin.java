package me.ddayo.aris.mixin;

import me.ddayo.aris.engine.hook.EntityHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerMixin {
    private static final long LEFT_CLICK_DEBOUNCE_TICKS = 4L;
    private static final Map<UUID, Long> LAST_LEFT_CLICK_TICK = new HashMap<>();
    private static final Map<UUID, BlockPos> ACTIVE_DESTROY_BLOCK = new HashMap<>();

    @Shadow
    public ServerPlayer player;

    @Inject(method = "handleAnimate", at = @At("HEAD"))
    private void onHandleAnimate(ServerboundSwingPacket packet, CallbackInfo ci) {
        if (packet.getHand() != InteractionHand.MAIN_HAND) return;
        MinecraftServer server = player.getServer();
        if (server.isSameThread()) {
            if (shouldFireLeftClick()) {
                EntityHooks.INSTANCE.executeOnLeftClick(player);
            }
        } else {
            server.execute(() -> {
                if (shouldFireLeftClick()) {
                    EntityHooks.INSTANCE.executeOnLeftClick(player);
                }
            });
        }
    }

    private boolean shouldFireLeftClick() {
        if (ACTIVE_DESTROY_BLOCK.containsKey(player.getUUID())) {
            return false;
        }
        long now = player.level().getGameTime();
        UUID uuid = player.getUUID();
        Long last = LAST_LEFT_CLICK_TICK.get(uuid);
        if (last != null && now - last < LEFT_CLICK_DEBOUNCE_TICKS) {
            return false;
        }
        LAST_LEFT_CLICK_TICK.put(uuid, now);
        return true;
    }

    @Inject(method = "handlePlayerAction", at = @At("HEAD"), cancellable = true)
    private void onHandlePlayerAction(ServerboundPlayerActionPacket packet, CallbackInfo ci) {
        ServerboundPlayerActionPacket.Action action = packet.getAction();
        if (action == ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK || action == ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK) {
            ACTIVE_DESTROY_BLOCK.remove(player.getUUID());
            return;
        }
        if (action != ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK) return;
        MinecraftServer server = player.getServer();
        if (server.isSameThread()) {
            if (!shouldFireBlockLeftClick(packet.getPos())) return;
            if (EntityHooks.INSTANCE.executeOnBlockLeftClick(player, packet.getPos(), packet.getDirection())) {
                ACTIVE_DESTROY_BLOCK.remove(player.getUUID());
                ci.cancel();
            } else {
                ACTIVE_DESTROY_BLOCK.put(player.getUUID(), packet.getPos().immutable());
            }
        } else {
            server.execute(() -> {
                if (shouldFireBlockLeftClick(packet.getPos()) && !EntityHooks.INSTANCE.executeOnBlockLeftClick(player, packet.getPos(), packet.getDirection())) {
                    ACTIVE_DESTROY_BLOCK.put(player.getUUID(), packet.getPos().immutable());
                }
            });
        }
    }

    private boolean shouldFireBlockLeftClick(BlockPos pos) {
        BlockPos activePos = ACTIVE_DESTROY_BLOCK.get(player.getUUID());
        return activePos == null || !activePos.equals(pos);
    }

    @Inject(method = "handleUseItemOn", at = @At("HEAD"), cancellable = true)
    private void onHandleUseItemOn(ServerboundUseItemOnPacket packet, CallbackInfo ci) {
        BlockHitResult hitResult = packet.getHitResult();
        MinecraftServer server = player.getServer();
        if (server.isSameThread()) {
            if (EntityHooks.INSTANCE.executeOnBlockRightClick(player, hitResult.getBlockPos(), hitResult.getDirection(), packet.getHand())) {
                ci.cancel();
            }
        } else {
            server.execute(() -> EntityHooks.INSTANCE.executeOnBlockRightClick(player, hitResult.getBlockPos(), hitResult.getDirection(), packet.getHand()));
        }
    }

    @Inject(method = "handleInteract", at = @At("HEAD"), cancellable = true)
    private void onHandleInteract(ServerboundInteractPacket packet, CallbackInfo ci) {
        MinecraftServer server = player.getServer();
        if (!server.isSameThread()) {
            server.execute(() -> handleEntityInteraction(packet));
            return;
        }
        if (handleEntityInteraction(packet)) {
            ci.cancel();
        }
    }

    private boolean handleEntityInteraction(ServerboundInteractPacket packet) {
        Entity target = packet.getTarget(player.serverLevel());
        if (target == null) return false;
        final boolean[] cancelled = {false};
        packet.dispatch(new ServerboundInteractPacket.Handler() {
            @Override
            public void onInteraction(InteractionHand hand) {
                cancelled[0] = EntityHooks.INSTANCE.executeOnEntityInteract(player, target, hand);
            }

            @Override
            public void onInteraction(InteractionHand hand, Vec3 location) {
                cancelled[0] = EntityHooks.INSTANCE.executeOnEntityInteract(player, target, hand);
            }

            @Override
            public void onAttack() {
                // Entity attack is handled by ServerPlayer.attack so code-triggered attacks are covered too.
            }
        });
        return cancelled[0];
    }

    @Inject(method = "handleChat", at = @At("HEAD"), cancellable = true)
    private void onHandleChat(ServerboundChatPacket packet, CallbackInfo ci) {
        MinecraftServer server = player.getServer();
        if (server.isSameThread()) {
            if (EntityHooks.INSTANCE.executeOnChat(player, packet.message())) {
                ci.cancel();
            }
        } else {
            server.execute(() -> EntityHooks.INSTANCE.executeOnChat(player, packet.message()));
        }
    }
}
