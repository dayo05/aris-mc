package me.ddayo.aris.engine.wrapper

import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.engine.wrapper.LuaEntity.Companion.toLuaValue
import me.ddayo.aris.lua.glue.InGameGenerated
import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.luagen.LuaProperty
import me.ddayo.aris.luagen.LuaProvider
import net.minecraft.world.damagesource.DamageSource


@LuaProvider(InGameEngine.PROVIDER)
class LuaDamageSource(val damageSource: DamageSource, @LuaProperty var amount: Float): ILuaStaticDecl by InGameGenerated.LuaDamageSource_LuaGenerated {
    @LuaProperty
    val causing get() = damageSource.entity?.toLuaValue()
    @LuaProperty
    val direct get() = damageSource.directEntity?.toLuaValue()
    @LuaProperty
    val isDirect get() = !damageSource.isIndirect
    @LuaProperty
    val id get() = damageSource.type().msgId
}