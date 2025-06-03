package me.ddayo.aris.engine.client.fabric

import me.ddayo.aris.client.fabric.ArisFabricClient
import me.ddayo.aris.engine.EngineInitializer
import me.ddayo.aris.engine.client.ClientInGameEngine
import me.ddayo.aris.engine.client.ClientInitEngine
import me.ddayo.aris.engine.client.ClientMainEngine

object ClientEngineAddOnImpl {
    @JvmStatic
    fun clientInitEngineAddOns(): List<EngineInitializer<ClientInitEngine>> = ArisFabricClient.INSTANCE.clientInitEngineAddOn

    @JvmStatic
    fun clientMainEngineAddOns(): List<EngineInitializer<ClientMainEngine>> = ArisFabricClient.INSTANCE.clientMainEngineAddOn

    @JvmStatic
    fun clientInGameEngineAddOns(): List<EngineInitializer<ClientInGameEngine>> = ArisFabricClient.INSTANCE.clientInGameEngineAddOn
}