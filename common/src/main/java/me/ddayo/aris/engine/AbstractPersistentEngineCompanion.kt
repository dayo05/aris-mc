package me.ddayo.aris.engine

import me.ddayo.aris.engine.hook.LuaHook
import me.ddayo.aris.engine.hook.LuaHookMap
import party.iroiro.luajava.Lua
import party.iroiro.luajava.luajit.LuaJit
import java.io.File
import kotlin.io.nameWithoutExtension

abstract class AbstractPersistentEngineCompanion<T : MCBaseEngine> {
    var INSTANCE: T? = null
        private set

    abstract val searchPath: String

    val disposeHook = mutableListOf<() -> Unit>()

    fun disposeEngine() {
        disposeHook.forEach { it() }
        hooks.forEach { it.clear() }
        hookMaps.forEach { it.clear() }
        INSTANCE = null
    }

    protected abstract fun _createEngine(lua: Lua): T

    fun createEngine(lua: Lua): T {
        return _createEngine(lua).apply {
            INSTANCE = this

            File(searchPath).listFiles()?.forEach {
                createTask(it, it.nameWithoutExtension)
            }
        }
    }

    fun reloadEngine() {
        disposeEngine()
        createEngine(LuaJit())
    }

    val hooks = mutableListOf<LuaHook>()
    val hookMaps = mutableListOf<LuaHookMap<*>>()
}