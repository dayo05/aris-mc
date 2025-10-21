package me.ddayo.aris.forge

import me.ddayo.aris.Aris
import me.ddayo.aris.client.ArisClient
import me.ddayo.aris.engine.EngineInitializer
import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.engine.InitEngine
import me.ddayo.aris.engine.MCBaseEngine
import me.ddayo.aris.engine.client.ClientInGameEngine
import me.ddayo.aris.engine.client.ClientInitEngine
import me.ddayo.aris.engine.client.ClientMainEngine
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent
import net.minecraftforge.fml.loading.FMLEnvironment
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import java.util.concurrent.CopyOnWriteArrayList

@net.minecraftforge.fml.common.Mod(Aris.MOD_ID)
class ArisForge {
    companion object {
        val initExtensions = CopyOnWriteArrayList<EngineInitializer<InitEngine>>()
        val inGameExtensions = CopyOnWriteArrayList<EngineInitializer<InGameEngine>>()
        @OnlyIn(Dist.CLIENT)
        val clientInitExtensions = CopyOnWriteArrayList<EngineInitializer<ClientInitEngine>>()
        @OnlyIn(Dist.CLIENT)
        val clientMainExtensions = CopyOnWriteArrayList<EngineInitializer<ClientMainEngine>>()
        @OnlyIn(Dist.CLIENT)
        val clientInGameExtensions = CopyOnWriteArrayList<EngineInitializer<ClientInGameEngine>>()
        fun<T> cloneExtensions(ex: CopyOnWriteArrayList<EngineInitializer<T>>) where T: MCBaseEngine = mutableListOf<EngineInitializer<T>>().apply {
            ex.forEach { add(it) }
        }.toList()
    }

    init {
        MOD_BUS.addListener { it: FMLConstructModEvent ->
            it.enqueueWork {
                Aris.init()
                if (FMLEnvironment.dist.isClient)
                    ArisClient.init()
            }
        }
        MOD_BUS.addListener { it: FMLLoadCompleteEvent ->
            it.enqueueWork {
                if (FMLEnvironment.dist.isClient)
                    ArisClient.onClientStart()
            }
        }

        RegistryHelperImpl.registries.forEach {
            it.register(MOD_BUS)
        }

        ArisForgeNetworking.register()
    }
}
