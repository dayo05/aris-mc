package me.ddayo.aris.engine.wrapper

import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.lua.glue.InGameGenerated
import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.luagen.LuaProperty
import me.ddayo.aris.luagen.LuaProvider

@LuaProvider(InGameEngine.PROVIDER)
class LuaItemConsumeEvent(
    val playerWrapper: LuaServerPlayer,
    val itemWrapper: LuaItemStack,
) : ILuaStaticDecl by InGameGenerated.LuaItemConsumeEvent_LuaGenerated {
    /**
     * 아이템을 소비한 플레이어
     */
    @LuaProperty
    val player get() = playerWrapper

    /**
     * 소비가 완료된 아이템
     */
    @LuaProperty
    val item get() = itemWrapper
}
