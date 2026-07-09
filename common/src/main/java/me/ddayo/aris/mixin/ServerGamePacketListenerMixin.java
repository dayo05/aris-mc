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
    private static final long BLOCK_ACTION_SWING_SUPPRESSION_TICKS = 8L;
    private static final Map<UUID, BlockPos> ACTIVE_DESTROY_BLOCK = new HashMap<>();
    private static final Map<UUID, Long> ACTIVE_DESTROY_TICK = new HashMap<>();
    private static final Map<UUID, Long> RECENT_BLOCK_ACTION_TICK = new HashMap<>();

    @Shadow
    public ServerPlayer player;

    @Inject(method = "handleAnimate", at = @At("HEAD"))
    private void onHandleAnimate(ServerboundSwingPacket packet, CallbackInfo ci) {
        if (packet.getHand() != InteractionHand.MAIN_HAND) return;
        MinecraftServer server = player.getServer();
        if (server.isSameThread()) {
            fireAirLeftClick();
        } else {
            server.execute(this::fireAirLeftClick);
        }
    }

    private void fireAirLeftClick() {
        if (hasActiveDestroyBlock() || hasRecentBlockAction()) {
            return;
        }
        EntityHooks.INSTANCE.executeOnLeftClickOncePerTick(player);
    }

    private boolean hasActiveDestroyBlock() {
        UUID uuid = player.getUUID();
        BlockPos activePos = ACTIVE_DESTROY_BLOCK.get(uuid);
        if (activePos == null) return false;
        Long startedAt = ACTIVE_DESTROY_TICK.get(uuid);
        if (startedAt == null) {
            ACTIVE_DESTROY_BLOCK.remove(uuid);
            ACTIVE_DESTROY_TICK.remove(uuid);
            return false;
        }
        return true;
    }

    private void markRecentBlockAction() {
        RECENT_BLOCK_ACTION_TICK.put(player.getUUID(), player.level().getGameTime());
    }

    private boolean hasRecentBlockAction() {
        UUID uuid = player.getUUID();
        Long actionAt = RECENT_BLOCK_ACTION_TICK.get(uuid);
        if (actionAt == null) return false;
        long age = player.level().getGameTime() - actionAt;
        if (age < 0 || age >= BLOCK_ACTION_SWING_SUPPRESSION_TICKS) {
            RECENT_BLOCK_ACTION_TICK.remove(uuid);
            return false;
        }
        markRecentBlockAction();
        return true;
    }

    @Inject(method = "handlePlayerAction", at = @At("HEAD"), cancellable = true)
    private void onHandlePlayerAction(ServerboundPlayerActionPacket packet, CallbackInfo ci) {
        ServerboundPlayerActionPacket.Action action = packet.getAction();
        if (action == ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK || action == ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK) {
            markRecentBlockAction();
            ACTIVE_DESTROY_BLOCK.remove(player.getUUID());
            ACTIVE_DESTROY_TICK.remove(player.getUUID());
            return;
        }
        if (action != ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK) return;
        markRecentBlockAction();
        MinecraftServer server = player.getServer();
        if (server.isSameThread()) {
            if (!shouldFireBlockLeftClick(packet.getPos())) return;
            EntityHooks.INSTANCE.executeOnLeftClickOncePerTick(player);
            if (EntityHooks.INSTANCE.executeOnBlockLeftClick(player, packet.getPos(), packet.getDirection())) {
                ACTIVE_DESTROY_BLOCK.remove(player.getUUID());
                ACTIVE_DESTROY_TICK.remove(player.getUUID());
                ci.cancel();
            } else {
                ACTIVE_DESTROY_BLOCK.put(player.getUUID(), packet.getPos().immutable());
                ACTIVE_DESTROY_TICK.put(player.getUUID(), player.level().getGameTime());
            }
        } else {
            server.execute(() -> {
                if (shouldFireBlockLeftClick(packet.getPos())) {
                    EntityHooks.INSTANCE.executeOnLeftClickOncePerTick(player);
                    if (!EntityHooks.INSTANCE.executeOnBlockLeftClick(player, packet.getPos(), packet.getDirection())) {
                        ACTIVE_DESTROY_BLOCK.put(player.getUUID(), packet.getPos().immutable());
                        ACTIVE_DESTROY_TICK.put(player.getUUID(), player.level().getGameTime());
                    }
                }
            });
        }
    }

    private boolean shouldFireBlockLeftClick(BlockPos pos) {
        hasActiveDestroyBlock();
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
