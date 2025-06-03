package me.ddayo.aris.engine.forge

import me.ddayo.aris.engine.EngineInitializer
import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.engine.InitEngine

object EngineAddOnImpl {
    val initEngineInitializers = mutableListOf<EngineInitializer<InitEngine>>()
    @JvmStatic
    fun initEngineAddOns()= initEngineInitializers

    val inGameEngineInitializers = mutableListOf<EngineInitializer<InGameEngine>>()
    @JvmStatic
    fun inGameEngineAddOns() = inGameEngineInitializers
}