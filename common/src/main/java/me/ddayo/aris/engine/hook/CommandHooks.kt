package me.ddayo.aris.engine.hook

import me.ddayo.aris.Aris
import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.luagen.LuaFunc
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import net.minecraft.resources.ResourceLocation

@LuaProvider(InGameEngine.PROVIDER, library = "aris.game.hook")
object CommandHooks {
    val commandEndpointHook = LuaHookMap<ResourceLocation>()
    init {
        InGameEngine.hookMaps.add(commandEndpointHook)
    }
    /**
     * 명령어를 입력했을때 실행할 함수를 지정합니다.
     * @param of 명령어 id
     * @param func 실행할 함수
     */
    @LuaFunction("register_endpoint")
    fun registerEndpoint(of: String, func: LuaFunc) {
        commandEndpointHook[ResourceLocation(Aris.MOD_ID, of)].add(func)
    }
}