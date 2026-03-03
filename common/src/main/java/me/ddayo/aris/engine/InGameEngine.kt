package me.ddayo.aris.engine

import me.ddayo.aris.engine.hook.LuaHook
import me.ddayo.aris.lua.glue.InGameGenerated
import party.iroiro.luajava.Lua
import java.io.File

class InGameEngine(lua: Lua) : MCBaseEngine(lua) {
    companion object: AbstractPersistantEngineCompanion<InGameEngine>() {
        const val PROVIDER = "InGameGenerated"
        override val searchPath = "robots/game"

        override fun _createEngine(lua: Lua) = InGameEngine(lua)

        val tickHook = LuaHook()
        init {
            hooks.add(tickHook)
        }
    }

    override val basePath = File(searchPath)

    init {
        InGameGenerated.initEngine(this)
        EngineAddOn.inGameEngineAddOns().forEach {
            it.initLua(this)
        }
    }

    fun tick() {
        tickHook.call()
    }
}