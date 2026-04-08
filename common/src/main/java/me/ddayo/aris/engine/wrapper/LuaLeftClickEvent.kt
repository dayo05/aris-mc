package me.ddayo.aris.engine.wrapper

import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.lua.glue.InGameGenerated
import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.luagen.LuaProperty
import me.ddayo.aris.luagen.LuaProvider

@LuaProvider(InGameEngine.PROVIDER)
class LuaLeftClickEvent(
    val playerWrapper: LuaServerPlayer
) : ILuaStaticDecl by InGameGenerated.LuaLeftClickEvent_LuaGenerated {
    /**
     * 좌클릭한 플레이어
     */
    @LuaProperty
    val player get() = playerWrapper
}
