package me.ddayo.aris.engine.hook

import me.ddayo.aris.luagen.LuaEngine
import me.ddayo.aris.luagen.LuaFunc
import me.ddayo.aris.util.ListExtensions.mutableForEach


class LuaHookMap<T> {
    private val inner = mutableMapOf<T, LuaHook>()
    operator fun get(of: T) = inner.getOrPut(of) { LuaHook() }

    fun clear() = inner.clear()
}

class LuaHook {
    private val inner = mutableListOf<LuaFunc>()

    fun add(f: LuaFunc) = inner.add(f)
    fun remove(f: LuaFunc) = inner.remove(f)
    fun clear() = inner.clear()

    fun call(vararg args: Any?) {
        inner.mutableForEach {
            it.call(*args)
        }
    }

    fun callAsTask(vararg args: Any?) {
        inner.mutableForEach {
            it.callAsTask(*args)
        }
    }

    fun callRawArg(arg: LuaFunc.() -> Int) {
        inner.mutableForEach {
            it.callRawArg(arg)
        }
    }

    fun callAsTaskRawArg(arg: LuaFunc.(task: LuaEngine.LuaTask) -> Int) {
        inner.mutableForEach {
            it.callAsTaskRawArg(arg)
        }
    }
}