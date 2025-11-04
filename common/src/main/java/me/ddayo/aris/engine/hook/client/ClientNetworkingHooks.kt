package me.ddayo.aris.engine.hook.client

import me.ddayo.aris.Aris
import me.ddayo.aris.engine.client.ClientInGameEngine
import me.ddayo.aris.engine.hook.LuaHookMap
import me.ddayo.aris.luagen.LuaFunc
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import net.minecraft.resources.ResourceLocation

@LuaProvider(ClientInGameEngine.PROVIDER, library = "aris.game.client.hook")
object ClientNetworkingHooks {
    val packetHooks = LuaHookMap<ResourceLocation>()
    init {
        ClientInGameEngine.hookMaps.add(packetHooks)
    }

    /**
     * 패킷이 서버로부터 전송됐을때 실행할 함수를 지정합니다.
     * @param id 패킷 id
     * @param func 실행할 함수
     */
    @LuaFunction("add_s2c_packet_handler")
    fun registerHandler(id: String, func: LuaFunc) {
        packetHooks[ResourceLocation(Aris.MOD_ID, id)].add(func)
    }
}