package me.ddayo.aris.engine.client

import dev.architectury.injectables.annotations.ExpectPlatform
import me.ddayo.aris.engine.EngineInitializer

object ClientEngineAddOn {
    @ExpectPlatform
    @JvmStatic
    fun clientInitEngineAddOns(): List<EngineInitializer<ClientInitEngine>> {
        throw NotImplementedError()
    }

    @ExpectPlatform
    @JvmStatic
    fun clientMainEngineAddOns(): List<EngineInitializer<ClientMainEngine>> {
        throw NotImplementedError()
    }

    @ExpectPlatform
    @JvmStatic
    fun clientInGameEngineAddOns(): List<EngineInitializer<ClientInGameEngine>> {
        throw NotImplementedError()
    }
}