package me.ddayo.aris.engine.wrapper

import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.lua.glue.InGameGenerated
import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.luagen.LuaProvider
import net.minecraft.server.level.ServerLevel

@LuaProvider(InGameEngine.PROVIDER)
class LuaServerWorld(val inner: ServerLevel) : ILuaStaticDecl by InGameGenerated.LuaServerWorld_LuaGenerated {
}