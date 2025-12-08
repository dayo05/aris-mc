package me.ddayo.aris.engine

import me.ddayo.aris.engine.hook.LuaHook
import me.ddayo.aris.engine.hook.LuaHookMap
import me.ddayo.aris.luagen.LuaFunc
import me.ddayo.aris.lua.glue.InGameGenerated
import me.ddayo.aris.util.ListExtensions.mutableForEach
import net.minecraft.resources.ResourceLocation
import party.iroiro.luajava.Lua
import java.io.File

class InGameEngine(lua: Lua) : MCBaseEngine(lua) {
    companion object {
        const val PROVIDER = "InGameGenerated"
        var INSTANCE: InGameEngine? = null
            private set

        val disposeHook = mutableListOf<() -> Unit>()

        fun disposeEngine() {
            disposeHook.forEach { it() }
            hooks.forEach { it.clear() }
            hookMaps.forEach { it.clear() }
            INSTANCE = null
        }

        fun createEngine(lua: Lua): InGameEngine {
            return InGameEngine(lua).apply {
                INSTANCE = this

                File("robots/game").listFiles()?.forEach {
                    createTask(it, it.nameWithoutExtension)
                }
            }
        }

        val hooks = mutableListOf<LuaHook>()
        val hookMaps = mutableListOf<LuaHookMap<*>>()
    }

    override val basePath = File("robots/game")

    init {
        InGameGenerated.initEngine(this)
        EngineAddOn.inGameEngineAddOns().forEach {
            it.initLua(this)
        }
    }

    val tickHook = LuaHook()
    fun tick() {
        tickHook.call()
    }
}