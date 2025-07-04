package me.ddayo.aris.engine

import me.ddayo.aris.luagen.LuaFunc
import me.ddayo.aris.lua.glue.InGameGenerated
import net.minecraft.resources.ResourceLocation
import party.iroiro.luajava.Lua
import java.io.File

class InGameEngine(lua: Lua): MCBaseEngine(lua) {
    companion object {
        const val PROVIDER = "InGameGenerated"
        var INSTANCE: InGameEngine? = null
            private set

        fun disposeEngine() {
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
    }

    override val basePath = File("robots/game")

    init {
        InGameGenerated.initEngine(this)
        EngineAddOn.inGameEngineAddOns().forEach {
            it.initLua(this)
        }
    }

    val itemUseHook = mutableMapOf<String, MutableList<LuaFunc>>()
    val packetFunctions = mutableMapOf<ResourceLocation, LuaFunc>()
    val commandFunctions = mutableMapOf<ResourceLocation, LuaFunc>()
}