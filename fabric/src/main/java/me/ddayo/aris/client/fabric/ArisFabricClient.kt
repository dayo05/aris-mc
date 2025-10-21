package me.ddayo.aris.client.fabric

import me.ddayo.aris.client.fabriclike.ArisFabricLikeClient
import me.ddayo.aris.engine.EngineInitializer
import me.ddayo.aris.engine.client.ClientInGameEngine
import me.ddayo.aris.engine.client.ClientInitEngine
import me.ddayo.aris.engine.client.ClientMainEngine
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import org.apache.logging.log4j.LogManager

class ArisFabricClient: ClientModInitializer {
    companion object {
        lateinit var INSTANCE: ArisFabricClient
            private set
    }
    init {
        INSTANCE = this
    }

    val clientInitEngineAddOn = mutableListOf<EngineInitializer<ClientInitEngine>>()
    val clientMainEngineAddOn = mutableListOf<EngineInitializer<ClientMainEngine>>()
    val clientInGameEngineAddOn = mutableListOf<EngineInitializer<ClientInGameEngine>>()
    override fun onInitializeClient() {
        ArisFabricLikeClient.init()
        val fabricLoader = FabricLoader.getInstance()
        fabricLoader.getEntrypointContainers("aris-client-init", EngineInitializer::class.java)
            .forEach {
                LogManager.getLogger().info("AddOn for client init engine registered: " + it.provider.metadata.id)
                clientInitEngineAddOn.add(it.entrypoint as EngineInitializer<ClientInitEngine>)
            }
        fabricLoader.getEntrypointContainers("aris-client-main", EngineInitializer::class.java)
            .forEach {
                LogManager.getLogger().info("AddOn for client main engine registered: " + it.provider.metadata.id)
                clientMainEngineAddOn.add(it.entrypoint as EngineInitializer<ClientMainEngine>)
            }
        fabricLoader.getEntrypointContainers("aris-client-game", EngineInitializer::class.java)
            .forEach {
                LogManager.getLogger().info("AddOn for client in-game engine registered: " + it.provider.metadata.id)
                clientInGameEngineAddOn.add(it.entrypoint as EngineInitializer<ClientInGameEngine>)
            }
    }
}