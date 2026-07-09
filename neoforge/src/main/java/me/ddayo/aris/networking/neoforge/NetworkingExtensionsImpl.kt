package me.ddayo.aris.networking.neoforge

import me.ddayo.aris.neoforge.ArisNeoForgeNetworking
import me.ddayo.aris.networking.LeftClickPayload
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.network.PacketDistributor

object NetworkingExtensionsImpl {
    @JvmStatic
    fun _sendDataPacket(player: ServerPlayer, of: String, data: Any) {
        ArisNeoForgeNetworking.sendDataPacketNeoForge(player, of, data)
    }

    @JvmStatic
    fun _sendReloadPacket(player: ServerPlayer) {
        ArisNeoForgeNetworking.sendReloadPacketNeoForge(player)
    }

    @JvmStatic
    fun _sendLeftClickPacket() {
        PacketDistributor.sendToServer(LeftClickPayload)
    }

    @JvmStatic
    fun<T: CustomPacketPayload> _registerPlayS2C(id: CustomPacketPayload.Type<T>, codec: StreamCodec<in RegistryFriendlyByteBuf, T>) {
        // NeoForge registers payloads via RegisterPayloadHandlersEvent, not manually.
        // The common Payloads are already registered in ArisNeoForgeNetworking.register()
    }

    @JvmStatic
    fun<T: CustomPacketPayload> _registerPlayC2S(id: CustomPacketPayload.Type<T>, codec: StreamCodec<in RegistryFriendlyByteBuf, T>) {
        // NeoForge registers payloads via RegisterPayloadHandlersEvent, not manually.
        // The common Payloads are already registered in ArisNeoForgeNetworking.register()
    }
}
