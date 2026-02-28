package me.ddayo.aris.engine.networking.fabric

import me.ddayo.aris.engine.networking.PacketDeclaration
import me.ddayo.aris.engine.wrapper.LuaServerPlayer
import me.ddayo.aris.networking.C2SLuaPayload
import me.ddayo.aris.networking.S2CLuaPayload
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.level.ServerPlayer
import org.apache.logging.log4j.LogManager

object S2CPacketSenderHandlerImpl {
    @JvmStatic
    fun sendS2CPacket(player: LuaServerPlayer, packet: PacketDeclaration.Builder) {
        val payload = S2CLuaPayload(dataToSend = packet)
        ServerPlayNetworking.send(player.player as ServerPlayer, payload)
    }
}

object C2SPacketSenderHandlerImpl {
    @JvmStatic
    fun sendC2SPacket(packet: PacketDeclaration.Builder) {
        val payload = C2SLuaPayload(dataToSend = packet)
        ClientPlayNetworking.send(payload)
    }
}
