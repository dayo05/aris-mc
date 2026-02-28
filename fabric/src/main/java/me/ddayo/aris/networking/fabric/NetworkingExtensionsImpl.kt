package me.ddayo.aris.networking.fabric

import me.ddayo.aris.fabric.ServerNetworkingFabric
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer

object NetworkingExtensionsImpl {
    @JvmStatic
    fun _sendDataPacket(player: ServerPlayer, of: String, data: Any) {
        ServerNetworkingFabric.sendDataPacketFabricLike(player, of, data)
    }

    @JvmStatic
    fun _sendReloadPacket(player: ServerPlayer) {
        ServerNetworkingFabric.sendReloadPacketFabricLike(player)
    }

    @JvmStatic
    fun<T: CustomPacketPayload> _registerPlayS2C(id: CustomPacketPayload.Type<T>, codec: StreamCodec<in RegistryFriendlyByteBuf, T>) {
        PayloadTypeRegistry.playS2C().register(id, codec)
    }

    @JvmStatic
    fun<T: CustomPacketPayload> _registerPlayC2S(id: CustomPacketPayload.Type<T>, codec: StreamCodec<in RegistryFriendlyByteBuf, T>) {
        PayloadTypeRegistry.playC2S().register(id, codec)
    }
}