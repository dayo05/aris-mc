package me.ddayo.aris.engine

import me.ddayo.aris.lua.glue.InitGenerated
import me.ddayo.aris.engine.item.InitAttributeBindings
import org.apache.logging.log4j.LogManager
import party.iroiro.luajava.Lua
import java.io.File

open class InitEngine protected constructor(lua: Lua) : MCBaseEngine(lua) {
    companion object {
        const val PROVIDER = "InitGenerated"
        fun create(lua: Lua) = InitEngine(lua).apply {
            File("robots/init").listFiles()?.forEach {
                createTask(it, it.nameWithoutExtension)
            }
        }
    }

    override val basePath = File("robots/init")

    init {
        InitGenerated.initEngine(this)
        InitAttributeBindings.install(this)
        EngineAddOn.initEngineAddOns().forEach {
            it.initLua(this)
        }
    }
}
