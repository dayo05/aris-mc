package me.ddayo.aris.engine.hook

import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.engine.wrapper.LuaServerPlayer
import me.ddayo.aris.luagen.LuaCallback
import me.ddayo.aris.luagen.LuaCallbackParam
import me.ddayo.aris.luagen.LuaFunc
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import net.minecraft.server.level.ServerPlayer

@LuaProvider(InGameEngine.PROVIDER, library = "aris.game.hook")
object GameHooks {
    /**
     * 매 틱마다 실행할 함수를 추가합니다.
     * @param f 실행할 함수
     */
    @LuaFunction("add_tick")
    fun addTickHook(@LuaCallback f: LuaFunc) {
        InGameEngine.tickHook.add(f)
    }

    /**
     * 매 틱마다 실행할 함수를 초기화합니다.
     */
    @LuaFunction("clear_tick")
    fun clearTickHook(@LuaCallback f: LuaFunc) {
        InGameEngine.tickHook.clear()
    }

    val playerJoinHook = LuaHook()
    val playerLeaveHook = LuaHook()
    init {
        InGameEngine.hooks.add(playerJoinHook)
        InGameEngine.hooks.add(playerLeaveHook)
    }

    /**
     * 플레이어가 서버에 접속했을 때 실행할 함수를 추가합니다.
     *
     * 엔진이 리로드(`/aris reload`)되면 새 엔진에서 이 함수가 다시 실행됩니다.
     * 이때 리로드 시점에 이미 접속해 있던 모든 플레이어에 대해서도 호출되므로,
     * 스크립트 입장에서 리로드는 새로 시작하는 것과 동일하게 취급됩니다.
     * @param f 실행할 함수 (접속한 LuaServerPlayer를 인자로 받음)
     */
    @LuaFunction("on_player_join_server")
    fun onPlayerJoinServer(
        @LuaCallback(params = [LuaCallbackParam("player", LuaServerPlayer::class)])
        f: LuaFunc
    ) {
        playerJoinHook.add(f)
    }

    /**
     * 플레이어가 서버에서 나갔을 때 실행할 함수를 추가합니다.
     *
     * 엔진이 리로드(`/aris reload`)되면 폐기되는 엔진에서 이 함수가 실행됩니다.
     * 이때 리로드 시점에 이미 접속해 있던 모든 플레이어에 대해서도 호출되므로,
     * 스크립트가 정리(cleanup) 작업을 수행할 수 있습니다.
     * @param f 실행할 함수 (나간 LuaServerPlayer를 인자로 받음)
     */
    @LuaFunction("on_player_leave_server")
    fun onPlayerLeaveServer(
        @LuaCallback(params = [LuaCallbackParam("player", LuaServerPlayer::class)])
        f: LuaFunc
    ) {
        playerLeaveHook.add(f)
    }

    fun executeOnPlayerJoin(player: ServerPlayer) {
        InGameEngine.INSTANCE ?: return
        playerJoinHook.callAsTask(LuaServerPlayer(player))
    }

    fun executeOnPlayerLeave(player: ServerPlayer) {
        InGameEngine.INSTANCE ?: return
        playerLeaveHook.callAsTask(LuaServerPlayer(player))
    }
}
