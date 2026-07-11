package me.ddayo.aris.engine.wrapper

import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.lua.glue.InGameGenerated
import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.luagen.LuaProperty
import me.ddayo.aris.luagen.LuaProvider

@LuaProvider(InGameEngine.PROVIDER)
class LuaSneakEvent(
    val playerWrapper: LuaServerPlayer,
    @LuaProperty("is_release") val isRelease: Boolean,
) : ILuaStaticDecl by InGameGenerated.LuaSneakEvent_LuaGenerated {
    /**
     * 이벤트를 발생시킨 플레이어
     */
    @LuaProperty
    val player get() = playerWrapper
}
