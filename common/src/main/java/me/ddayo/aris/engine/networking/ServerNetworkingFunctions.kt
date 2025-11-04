package me.ddayo.aris.engine.networking

import dev.architectury.injectables.annotations.ExpectPlatform
import me.ddayo.aris.Aris
import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.engine.hook.ServerNetworkingHooks
import me.ddayo.aris.engine.wrapper.LuaServerPlayer
import me.ddayo.aris.luagen.LuaFunc
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import net.minecraft.resources.ResourceLocation
import org.apache.logging.log4j.LogManager

@LuaProvider(InGameEngine.PROVIDER, library = "aris.game.networking")
object C2SPacketReceiverHandlerLegacy {
    /**
     * 패킷이 클라이언트로부터 전송됐을때 실행할 함수를 지정합니다.
     * @param id 패킷 id
     * @param func 실행할 함수
     */
    @LuaFunction("register_c2s_packet_handler")
    fun registerHandler(id: String, func: LuaFunc) {
        LogManager.getLogger().warn("Use aris.game.hook.add_c2s_packet_handler instead.")
        ServerNetworkingHooks.registerHandler(id, func)
    }
}

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
        return S2CPacketHandler.packets[ResourceLocation(Aris.MOD_ID, of)]!!.Builder()
    }
}
