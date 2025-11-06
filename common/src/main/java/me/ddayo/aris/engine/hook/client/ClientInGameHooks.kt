package me.ddayo.aris.engine.hook.client

import me.ddayo.aris.engine.client.ClientInGameEngine
import me.ddayo.aris.engine.client.ClientMainEngine
import me.ddayo.aris.engine.hook.LuaHookMap
import me.ddayo.aris.luagen.LuaFunc
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import me.ddayo.aris.luagen.RetrieveEngine

@LuaProvider(ClientMainEngine.PROVIDER, library = "aris.game.client.hook")
object ClientInGameHooks {
    /**
     * 매 틱마다 실행할 함수를 추가합니다.
     * @param f 실행할 함수
     */
    @LuaFunction("add_tick_hook")
    fun addTickHook(@RetrieveEngine engine: ClientInGameEngine, f: LuaFunc) {
        engine.tickFunctions.add(f)
    }

    /**
     * 매 틱마다 실행할 함수를 초기화합니다.
     */
    @LuaFunction("clear_tick_hook")
    fun clearTickHook(@RetrieveEngine engine: ClientInGameEngine, f: LuaFunc) {
        engine.tickFunctions.clear()
    }

    /**
     * 새로 추가한 조작키를 실행할때 실행될 함수를 지정합니다.
     * @param key 누를 키
     * @param function 실행할 함수
     */
    @LuaFunction("on_key_pressed")
    fun onKeyPressed(@RetrieveEngine engine: ClientInGameEngine, key: String, function: LuaFunc) {
        engine.registerKeyHook(key, function)
    }
}