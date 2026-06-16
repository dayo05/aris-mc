package me.ddayo.aris.engine.wrapper

import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.lua.glue.InGameGenerated
import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProperty
import me.ddayo.aris.luagen.LuaProvider

@LuaProvider(InGameEngine.PROVIDER)
class LuaChatEvent(
    val playerWrapper: LuaServerPlayer,
    @LuaProperty val message: String,
) : ILuaStaticDecl by InGameGenerated.LuaChatEvent_LuaGenerated {
    /**
     * 채팅을 보낸 플레이어
     */
    @LuaProperty
    val player get() = playerWrapper

    var cancelled = false
        private set

    /**
     * 채팅 전송을 취소합니다.
     */
    @LuaFunction("cancel")
    fun cancel() {
        cancelled = true
    }
}
