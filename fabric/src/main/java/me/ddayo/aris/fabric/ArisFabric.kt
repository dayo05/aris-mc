package me.ddayo.aris.fabric

import me.ddayo.aris.engine.EngineInitializer
import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.engine.InitEngine
import me.ddayo.aris.fabriclike.ArisFabricLike
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import org.apache.logging.log4j.LogManager

class ArisFabric: ModInitializer {
    companion object {
        lateinit var INSTANCE: ArisFabric
    }
    init {
        INSTANCE = this
    }

    val initEngineAddOn = mutableListOf<EngineInitializer<InitEngine>>()
    val inGameEngineAddOn = mutableListOf<EngineInitializer<InGameEngine>>()

    override fun onInitialize() {
        val fabricLoader = FabricLoader.getInstance()
        fabricLoader.getEntrypointContainers("aris-init", EngineInitializer::class.java)
            .forEach {
                LogManager.getLogger().info("AddOn for init engine registered: " + it.provider.metadata.id)
                initEngineAddOn.add(it.entrypoint as EngineInitializer<InitEngine>)
            }
        fabricLoader.getEntrypointContainers("aris-game", EngineInitializer::class.java)
            .forEach {
                LogManager.getLogger().info("AddOn for in-game engine registered: " + it.provider.metadata.id)
                inGameEngineAddOn.add(it.entrypoint as EngineInitializer<InGameEngine>)
            }
        ArisFabricLike.init()
    }
}