package me.ddayo.aris.engine.wrapper

import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.lua.glue.InGameGenerated
import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.luagen.LuaProperty
import me.ddayo.aris.luagen.LuaProvider

@LuaProvider(InGameEngine.PROVIDER)
class LuaPlayerEvent(
    val playerWrapper: LuaServerPlayer,
    @LuaProperty val action: String,
) : ILuaStaticDecl by InGameGenerated.LuaPlayerEvent_LuaGenerated {
    /**
     * 이벤트를 발생시킨 플레이어
     */
    @LuaProperty
    val player get() = playerWrapper
}
