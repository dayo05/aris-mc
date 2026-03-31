package me.ddayo.aris.engine.networking

import dev.architectury.injectables.annotations.ExpectPlatform
import me.ddayo.aris.RegistryHelper
import me.ddayo.aris.engine.client.ClientInGameEngine
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider

@LuaProvider(ClientInGameEngine.PROVIDER, library = "aris.game.client.networking")
object C2SPacketSenderHandler {
    /**
     * 서버로 주어진 패킷을 전송합니다.
     * @param packet 패킷
     */
    @LuaFunction("send_c2s_packet")
    @ExpectPlatform
    @JvmStatic
    fun sendC2SPacket(packet: PacketDeclaration.Builder) {
        throw NotImplementedError()
    }

    /**
     * 서버로 전송할 패킷을 설정하는 빌더(builder)를 만듭니다.
     * @param of 전송할 패킷의 id
     */
    @LuaFunction("create_c2s_packet_builder")
    fun createPacketBuilder(of: String): PacketDeclaration.Builder {
        return C2SPacketHandler.packets[RegistryHelper.getResourceLocation(of)]!!.Builder()
    }
}

