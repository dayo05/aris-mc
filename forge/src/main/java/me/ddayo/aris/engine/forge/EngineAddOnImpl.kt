package me.ddayo.aris.engine.forge

import me.ddayo.aris.engine.EngineInitializer
import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.engine.InitEngine
import me.ddayo.aris.forge.ArisForge

object EngineAddOnImpl {
    @JvmStatic
    fun initEngineAddOns() = ArisForge.cloneExtensions(ArisForge.initExtensions)
    @JvmStatic
    fun inGameEngineAddOns() = ArisForge.cloneExtensions(ArisForge.inGameExtensions)
}