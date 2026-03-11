package me.ddayo.aris.engine.neoforge

import me.ddayo.aris.engine.EngineInitializer
import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.engine.InitEngine
import me.ddayo.aris.neoforge.ArisNeoForge

object EngineAddOnImpl {
    @JvmStatic
    fun initEngineAddOns() = ArisNeoForge.cloneExtensions(ArisNeoForge.initExtensions)
    @JvmStatic
    fun inGameEngineAddOns() = ArisNeoForge.cloneExtensions(ArisNeoForge.inGameExtensions)
}
