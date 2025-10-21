package me.ddayo.aris.engine.fabric

import me.ddayo.aris.engine.EngineInitializer
import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.engine.InitEngine
import me.ddayo.aris.fabric.ArisFabric

object EngineAddOnImpl {
    @JvmStatic
    fun initEngineAddOns(): List<EngineInitializer<InitEngine>> = ArisFabric.INSTANCE.initEngineAddOn
    @JvmStatic
    fun inGameEngineAddOns(): List<EngineInitializer<InGameEngine>> = ArisFabric.INSTANCE.inGameEngineAddOn
}