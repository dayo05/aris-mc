package me.ddayo.aris.engine.client.neoforge

import me.ddayo.aris.engine.EngineInitializer
import me.ddayo.aris.engine.client.ClientInGameEngine
import me.ddayo.aris.engine.client.ClientInitEngine
import me.ddayo.aris.engine.client.ClientMainEngine
import me.ddayo.aris.neoforge.ArisNeoForge

object ClientEngineAddOnImpl {
    @JvmStatic
    fun clientInitEngineAddOns() = ArisNeoForge.cloneExtensions(ArisNeoForge.clientInitExtensions)

    @JvmStatic
    fun clientMainEngineAddOns() = ArisNeoForge.cloneExtensions(ArisNeoForge.clientMainExtensions)

    @JvmStatic
    fun clientInGameEngineAddOns() = ArisNeoForge.cloneExtensions(ArisNeoForge.clientInGameExtensions)
}
