package me.ddayo.aris.engine.client.forge

import me.ddayo.aris.engine.EngineInitializer
import me.ddayo.aris.engine.client.ClientInGameEngine
import me.ddayo.aris.engine.client.ClientInitEngine
import me.ddayo.aris.engine.client.ClientMainEngine

object ClientEngineAddOnImpl {
    val clientInitEngineInitializers = mutableListOf<EngineInitializer<ClientInitEngine>>()
    @JvmStatic
    fun clientInitEngineAddOns()= clientInitEngineInitializers

    val clientMainEngineInitializers = mutableListOf<EngineInitializer<ClientMainEngine>>()
    @JvmStatic
    fun clientMainEngineAddOns()= clientMainEngineInitializers

    val clientInGameEngineInitializers = mutableListOf<EngineInitializer<ClientInGameEngine>>()
    @JvmStatic
    fun clientInGameEngineAddOns() = clientInGameEngineInitializers
}