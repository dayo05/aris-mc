package me.ddayo.aris.engine.hook.client

import me.ddayo.aris.engine.client.ClientInGameEngine
import me.ddayo.aris.engine.client.ClientMainEngine
import me.ddayo.aris.engine.hook.LuaHook
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
    fun addTickHook(f: LuaFunc) {
        ClientInGameEngine.tickHook.add(f)
    }

    /**
     * 매 틱마다 실행할 함수를 초기화합니다.
     */
    @LuaFunction("clear_tick_hook")
    fun clearTickHook(f: LuaFunc) {
        ClientInGameEngine.tickHook.clear()
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

    val playerLeaveHook = LuaHook()
    init {
        ClientInGameEngine.hooks.add(playerLeaveHook)
    }

    /**
     * 클라이언트가 서버에서 나갔을 때 실행할 함수를 추가합니다.
     *
     * 엔진이 리로드되면 폐기되는 엔진에서 이 함수가 실행되므로,
     * 스크립트가 정리(cleanup) 작업을 수행할 수 있습니다.
     * @param f 실행할 함수
     */
    @LuaFunction("on_player_leave_server")
    fun onPlayerLeaveServer(f: LuaFunc) {
        playerLeaveHook.add(f)
    }

    fun executeOnPlayerLeave() {
        playerLeaveHook.call()
    }
}