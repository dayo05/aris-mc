package me.ddayo.aris.engine.hook

import me.ddayo.aris.Aris
import me.ddayo.aris.RegistryHelper
import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.engine.wrapper.LuaServerPlayer
import me.ddayo.aris.luagen.LuaCallback
import me.ddayo.aris.luagen.LuaCallbackParam
import me.ddayo.aris.luagen.LuaFunc
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import me.ddayo.aris.luagen.LuaType
import net.minecraft.resources.ResourceLocation

@LuaProvider(InGameEngine.PROVIDER, library = "aris.game.hook")
object ServerNetworkingHooks {
    val packetHooks = LuaHookMap<ResourceLocation>()
    init {
        InGameEngine.hookMaps.add(packetHooks)
    }
    /**
     * 패킷이 클라이언트로부터 전송됐을때 실행할 함수를 지정합니다.
     * @param id 패킷 id
     * @param func 실행할 함수
     */
    @LuaFunction("add_c2s_packet_handler")
    fun registerHandler(
        id: String,
        @LuaCallback(params = [
            LuaCallbackParam("player", LuaServerPlayer::class),
            LuaCallbackParam("packet", luaType = LuaType.TABLE)
        ])
        func: LuaFunc
    ) {
        packetHooks[RegistryHelper.getResourceLocation(id)].add(func)
    }
}
