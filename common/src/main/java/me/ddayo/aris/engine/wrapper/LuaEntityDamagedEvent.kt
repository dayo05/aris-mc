package me.ddayo.aris.engine.wrapper

import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.lua.glue.InGameGenerated
import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.luagen.LuaProperty
import me.ddayo.aris.luagen.LuaProvider

@LuaProvider(InGameEngine.PROVIDER)
class LuaEntityDamagedEvent(
    val damageSourceWrapper: LuaDamageSource,
    val targetWrapper: LuaEntity
) : ILuaStaticDecl by InGameGenerated.LuaEntityDamagedEvent_LuaGenerated {
    /**
     * 데미지 정보. amount를 수정하면 데미지가 변경됩니다.
     */
    @LuaProperty
    val damage get() = damageSourceWrapper

    /**
     * 데미지를 받은 엔티티
     */
    @LuaProperty
    val target get() = targetWrapper
}
