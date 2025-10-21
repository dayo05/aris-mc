package me.ddayo.aris.engine

import dev.architectury.injectables.annotations.ExpectPlatform

object EngineAddOn {
    @ExpectPlatform
    @JvmStatic
    fun initEngineAddOns(): List<EngineInitializer<InitEngine>> {
        throw NotImplementedError()
    }

    @ExpectPlatform
    @JvmStatic
    fun inGameEngineAddOns(): List<EngineInitializer<InGameEngine>> {
        throw NotImplementedError()
    }
}