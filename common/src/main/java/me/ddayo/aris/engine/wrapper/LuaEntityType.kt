package me.ddayo.aris.engine.wrapper

import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.lua.glue.InGameGenerated
import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.luagen.LuaProvider
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType

@LuaProvider(InGameEngine.PROVIDER)
class LuaEntityType<T : Entity>(val inner: EntityType<T>) : ILuaStaticDecl by InGameGenerated.LuaEntityType_LuaGenerated {

}