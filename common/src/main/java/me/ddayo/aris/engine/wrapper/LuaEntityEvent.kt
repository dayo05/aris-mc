package me.ddayo.aris.engine.wrapper

import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.lua.glue.InGameGenerated
import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.luagen.LuaProperty
import me.ddayo.aris.luagen.LuaProvider

@LuaProvider(InGameEngine.PROVIDER)
class LuaEntityEvent(
    val entityWrapper: LuaEntity,
    @LuaProperty val action: String,
) : ILuaStaticDecl by InGameGenerated.LuaEntityEvent_LuaGenerated {
    /**
     * 사망한 엔티티
     */
    @LuaProperty
    val entity get() = entityWrapper

    /**
     * 사망한 엔티티가 플레이어이면 해당 플레이어를 반환합니다.
     * 플레이어가 아니면 nil을 반환합니다.
     */
    @LuaProperty
    val player get() = entityWrapper as? LuaServerPlayer
}
