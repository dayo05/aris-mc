package me.ddayo.aris.engine.wrapper

import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.lua.glue.InGameGenerated
import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProperty
import me.ddayo.aris.luagen.LuaProvider

@LuaProvider(InGameEngine.PROVIDER)
class LuaEntityInteractEvent(
    val playerWrapper: LuaServerPlayer,
    val targetWrapper: LuaEntity,
    @LuaProperty val action: String,
    @LuaProperty val hand: String,
) : ILuaStaticDecl by InGameGenerated.LuaEntityInteractEvent_LuaGenerated {
    /**
     * 이벤트를 발생시킨 플레이어
     */
    @LuaProperty
    val player get() = playerWrapper

    /**
     * 상호작용 대상 엔티티
     */
    @LuaProperty
    val target get() = targetWrapper

    var cancelled = false
        private set

    /**
     * 이벤트를 취소합니다.
     */
    @LuaFunction("cancel")
    fun cancel() {
        cancelled = true
    }
}
