package me.ddayo.aris.engine.wrapper

import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.lua.glue.InGameGenerated
import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.luagen.LuaProperty
import me.ddayo.aris.luagen.LuaProvider

@LuaProvider(InGameEngine.PROVIDER)
class LuaRightClickEvent(
    val playerWrapper: LuaServerPlayer
) : ILuaStaticDecl by InGameGenerated.LuaRightClickEvent_LuaGenerated {
    /**
     * 우클릭한 플레이어
     */
    @LuaProperty
    val player get() = playerWrapper
}
