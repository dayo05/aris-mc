package me.ddayo.aris.engine.wrapper

import me.ddayo.aris.Aris
import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.lua.glue.InGameGenerated
import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProperty
import me.ddayo.aris.luagen.LuaProvider
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level

@LuaProvider(InGameEngine.PROVIDER)
class LuaServerWorld(val inner: ServerLevel) : ILuaStaticDecl by InGameGenerated.LuaServerWorld_LuaGenerated {

}

@LuaProvider(InGameEngine.PROVIDER, library = "aris.game.world")
object LuaServerWorldFunctions {
    @LuaProperty
    val overworld = Aris.server.getLevel(Level.OVERWORLD)?.let { LuaServerWorld(it) }

    @LuaProperty
    val nether = Aris.server.getLevel(Level.NETHER)?.let { LuaServerWorld(it) }

    @LuaProperty
    val end = Aris.server.getLevel(Level.END)?.let { LuaServerWorld(it) }

    @LuaFunction("get_world")
    fun getWorld(world: String) = Aris.server.getLevel(ResourceKey.create(Registries.DIMENSION, ResourceLocation(world)))?.let { LuaServerWorld(it) }
}