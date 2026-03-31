package me.ddayo.aris.engine.wrapper

import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.lua.glue.InGameGenerated
import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.luagen.LuaProperty
import me.ddayo.aris.luagen.LuaProvider

@LuaProvider(InGameEngine.PROVIDER)
class LuaUseItemEvent(
    val playerWrapper: LuaServerPlayer,
    val itemWrapper: LuaItemStack
) : ILuaStaticDecl by InGameGenerated.LuaUseItemEvent_LuaGenerated {
    /**
     * 아이템을 사용한 플레이어
     */
    @LuaProperty
    val player get() = playerWrapper

    /**
     * 사용한 아이템
     */
    @LuaProperty
    val item get() = itemWrapper
}
