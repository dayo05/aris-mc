package me.ddayo.aris.engine.wrapper

import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.lua.glue.InGameGenerated
import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProperty
import me.ddayo.aris.luagen.LuaProvider

@LuaProvider(InGameEngine.PROVIDER)
class LuaItemMoveEvent(
    val playerWrapper: LuaServerPlayer,
    val itemWrapper: LuaItemStack,
    val moveType: String
) : ILuaStaticDecl by InGameGenerated.LuaItemMoveEvent_LuaGenerated {
    var cancelled = false

    /**
     * 이벤트를 발생시킨 플레이어
     */
    @LuaProperty
    val player get() = playerWrapper

    /**
     * 이동 대상 아이템
     */
    @LuaProperty
    val item get() = itemWrapper

    /**
     * 이동 유형: "container_click", "drop", "pickup"
     */
    @LuaProperty
    val type get() = moveType

    /**
     * 이벤트를 취소합니다.
     */
    @LuaFunction("cancel")
    fun cancel() {
        cancelled = true
    }
}
