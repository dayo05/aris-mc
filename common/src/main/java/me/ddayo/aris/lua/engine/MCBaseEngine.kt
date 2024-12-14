package me.ddayo.aris.lua.engine

import me.ddayo.aris.LuaEngine
import me.ddayo.aris.lua.glue.LuaGenerated
import org.apache.logging.log4j.LogManager
import party.iroiro.luajava.Lua
import java.io.File

open class MCBaseEngine(lua: Lua) : LuaEngine(lua) {
    init {
        LuaGenerated.initLua(lua)
    }

    fun createTask(file: File, _name: String? = null) {
        if(!file.exists()) return
        val name = _name ?: file.name
        LogManager.getLogger().info("Task ${createTask(file.readText(), name)} created")
    }
}