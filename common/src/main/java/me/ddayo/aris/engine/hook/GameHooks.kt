package me.ddayo.aris.engine.hook

import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.luagen.LuaFunc
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import me.ddayo.aris.luagen.RetrieveEngine

@LuaProvider(InGameEngine.PROVIDER, library = "aris.game.hook")
object GameHooks {
    /**
     * 매 틱마다 실행할 함수를 추가합니다.
     * @param f 실행할 함수
     */
    @LuaFunction("add_tick")
    fun addTickHook(@RetrieveEngine engine: InGameEngine, f: LuaFunc) {
        engine.tickHook.add(f)
    }

    /**
     * 매 틱마다 실행할 함수를 초기화합니다.
     */
    @LuaFunction("clear_tick")
    fun clearTickHook(@RetrieveEngine engine: InGameEngine, f: LuaFunc) {
        engine.tickHook.clear()
    }
}