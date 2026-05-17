package me.ddayo.aris.engine.hook

import me.ddayo.aris.engine.MCBaseEngine
import me.ddayo.aris.luagen.LuaFunc
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import me.ddayo.aris.luagen.RetrieveEngine

/**
 * 모든 엔진 종류(init 엔진 포함)에서 공통으로 사용할 수 있는 훅입니다.
 * 기본 프로바이더에 등록되므로 별도 라이브러리 없이 모든 엔진에서 호출할 수 있습니다.
 */
@LuaProvider(library = "aris.hook")
object EngineHooks {
    /**
     * 엔진이 폐기될 때 실행할 함수를 추가합니다.
     *
     * - in-game / client-main 엔진: 엔진이 dispose될 때(예: `/aris reload`, 게임 퇴장) 실행됩니다.
     * - init / client-init 엔진: 초기화 단계가 끝난 직후 실행됩니다.
     *
     * 엔진이 폐기되는 중에 동기적으로 실행되므로 정리(cleanup) 용도로 사용하세요.
     * 이 콜백 안에서는 task_yield 등으로 yield할 수 없습니다.
     * @param f 실행할 함수
     */
    @LuaFunction("on_engine_dispose")
    fun onEngineDispose(@RetrieveEngine engine: MCBaseEngine, f: LuaFunc) {
        engine.addDisposeCallback(f)
    }
}
