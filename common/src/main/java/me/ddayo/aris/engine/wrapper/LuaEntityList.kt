package me.ddayo.aris.engine.wrapper

import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.engine.wrapper.LuaEntity.Companion.toLuaValue
import me.ddayo.aris.lua.glue.InGameGenerated
import me.ddayo.aris.luagen.CoroutineProvider
import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.luagen.LuaCallback
import me.ddayo.aris.luagen.LuaCallbackParam
import me.ddayo.aris.luagen.LuaFunc
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProperty
import me.ddayo.aris.luagen.LuaProvider
import net.minecraft.world.entity.Entity

@LuaProvider(InGameEngine.PROVIDER)
class LuaEntityList(private val entities: List<Entity>) : CoroutineProvider,
    ILuaStaticDecl by InGameGenerated.LuaEntityList_LuaGenerated {

    /**
     * selector 결과 엔티티 수를 가져옵니다.
     */
    @LuaProperty
    val size get() = entities.size

    /**
     * selector 결과에서 1부터 시작하는 index의 엔티티를 가져옵니다.
     * 범위를 벗어나면 null을 반환합니다.
     * @param index 1부터 시작하는 index
     */
    @LuaFunction("get")
    fun get(index: Int): LuaEntity? = entities.getOrNull(index - 1)?.toLuaValue()

    /**
     * selector 결과의 모든 엔티티를 순회합니다.
     * @param fn 각 엔티티에 대해 실행할 콜백
     */
    @LuaFunction("iter")
    fun iter(
        @LuaCallback(params = [LuaCallbackParam("entity", LuaEntity::class)])
        fn: LuaFunc
    ) = coroutine<Unit> {
        entities.forEach {
            yield(fn.await(this@coroutine, it.toLuaValue()))
        }
    }
}
