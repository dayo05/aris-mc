package me.ddayo.aris.engine.networking.neoforge

import me.ddayo.aris.engine.networking.PacketDeclaration
import me.ddayo.aris.engine.wrapper.LuaServerPlayer
import me.ddayo.aris.networking.C2SLuaPayload
import me.ddayo.aris.networking.S2CLuaPayload
import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.network.PacketDistributor

object S2CPacketSenderHandlerImpl {
    @JvmStatic
    fun sendS2CPacket(player: LuaServerPlayer, packet: PacketDeclaration.Builder) {
        val payload = S2CLuaPayload(dataToSend = packet)
        PacketDistributor.sendToPlayer(player.player as ServerPlayer, payload)
    }
}

object C2SPacketSenderHandlerImpl {
    @JvmStatic
    fun sendC2SPacket(packet: PacketDeclaration.Builder) {
        val payload = C2SLuaPayload(dataToSend = packet)
        PacketDistributor.sendToServer(payload)
    }
}
