package me.ddayo.aris.engine.client.forge

import me.ddayo.aris.engine.EngineInitializer
import me.ddayo.aris.engine.client.ClientInGameEngine
import me.ddayo.aris.engine.client.ClientInitEngine
import me.ddayo.aris.engine.client.ClientMainEngine
import me.ddayo.aris.forge.ArisForge

object ClientEngineAddOnImpl {
    @JvmStatic
    fun clientInitEngineAddOns() = ArisForge.cloneExtensions(ArisForge.clientInitExtensions)

    @JvmStatic
    fun clientMainEngineAddOns() = ArisForge.cloneExtensions(ArisForge.clientMainExtensions)

    @JvmStatic
    fun clientInGameEngineAddOns() = ArisForge.cloneExtensions(ArisForge.clientInGameExtensions)
}