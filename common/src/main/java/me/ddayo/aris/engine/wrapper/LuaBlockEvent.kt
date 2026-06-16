package me.ddayo.aris.engine.wrapper

import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.lua.glue.InGameGenerated
import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProperty
import me.ddayo.aris.luagen.LuaProvider

@LuaProvider(InGameEngine.PROVIDER)
class LuaBlockEvent(
    val playerWrapper: LuaServerPlayer,
    @LuaProperty val x: Int,
    @LuaProperty val y: Int,
    @LuaProperty val z: Int,
    @LuaProperty("block_id") val blockId: String,
    @LuaProperty val face: String,
    @LuaProperty val action: String,
    @LuaProperty val hand: String,
) : ILuaStaticDecl by InGameGenerated.LuaBlockEvent_LuaGenerated {
    @LuaProperty
    val player get() = playerWrapper

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
