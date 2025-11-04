package me.ddayo.aris.engine.networking

import dev.architectury.injectables.annotations.ExpectPlatform
import me.ddayo.aris.Aris
import me.ddayo.aris.engine.client.ClientInGameEngine
import me.ddayo.aris.engine.hook.client.ClientNetworkingHooks
import me.ddayo.aris.luagen.LuaFunc
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import net.minecraft.resources.ResourceLocation
import org.apache.logging.log4j.LogManager

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
        return C2SPacketHandler.packets[ResourceLocation(Aris.MOD_ID, of)]!!.Builder()
    }
}

@LuaProvider(ClientInGameEngine.PROVIDER, library = "aris.game.client.networking")
object S2CPacketReceiverHandlerLegacy {
    /**
     * 패킷이 서버로부터 전송됐을때 실행할 함수를 지정합니다.
     * @param id 패킷 id
     * @param func 실행할 함수
     */
    @LuaFunction("register_s2c_packet_handler")
    fun registerHandler(id: String, func: LuaFunc) {
        LogManager.getLogger().warn("Use aris.game.client.hook.add_s2c_packet_handler")
        ClientNetworkingHooks.registerHandler(id, func)
    }
}
