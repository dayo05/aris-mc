package me.ddayo.aris.networking

import dev.architectury.injectables.annotations.ExpectPlatform
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer

object NetworkingExtensions {
    fun ServerPlayer.sendDataPacket(of: String, data: Any) {
        _sendDataPacket(this, of, data)
    }

    @JvmStatic
    @ExpectPlatform
    fun _sendDataPacket(player: ServerPlayer, of: String, data: Any) {
        throw NotImplementedError()
    }

    fun ServerPlayer.sendReloadPacket() {
        _sendReloadPacket(this)
    }

    @JvmStatic
    @ExpectPlatform
    fun _sendReloadPacket(player: ServerPlayer) {
        throw NotImplementedError()
    }

    @JvmStatic
    @ExpectPlatform
    fun<T: CustomPacketPayload> _registerPlayS2C(id: CustomPacketPayload.Type<T>, codec: StreamCodec<in RegistryFriendlyByteBuf, T>) {
        throw NotImplementedError()
    }

    @JvmStatic
    @ExpectPlatform
    fun<T: CustomPacketPayload> _registerPlayC2S(id: CustomPacketPayload.Type<T>, codec: StreamCodec<in RegistryFriendlyByteBuf, T>) {
        throw NotImplementedError()
    }
}