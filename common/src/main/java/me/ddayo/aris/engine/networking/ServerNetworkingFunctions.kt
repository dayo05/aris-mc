package me.ddayo.aris.engine.networking

import dev.architectury.injectables.annotations.ExpectPlatform
import me.ddayo.aris.RegistryHelper
import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.engine.wrapper.LuaServerPlayer
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider

@LuaProvider(InGameEngine.PROVIDER, library = "aris.game.networking")
object S2CPacketSenderHandler {
    /**
     * 클라이언트로 주어진 패킷을 전송합니다.
     * @param player 타겟 플레이어
     * @param packet 패킷
     */
    @LuaFunction("send_s2c_packet")
    @ExpectPlatform
    @JvmStatic
    fun sendS2CPacket(player: LuaServerPlayer, packet: PacketDeclaration.Builder) {
        throw NotImplementedError()
    }

    /**
     * 클라이언트로 전송할 패킷을 설정하는 빌더(builder)를 만듭니다.
     * @param of 전송할 패킷의 id
     */
    @LuaFunction("create_s2c_packet_builder")
    fun createPacketBuilder(of: String): PacketDeclaration.Builder {
        return S2CPacketHandler.packets[RegistryHelper.getResourceLocation(of)]!!.Builder()
    }
}
