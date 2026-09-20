package me.ddayo.aris.neoforge

import me.ddayo.aris.Aris
import me.ddayo.aris.client.ArisClient
import me.ddayo.aris.client.neoforge.ArisNeoForgeClientInitEvents
import me.ddayo.aris.engine.EngineInitializer
import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.engine.InitEngine
import me.ddayo.aris.engine.MCBaseEngine
import me.ddayo.aris.engine.item.CreativeTabRegistry
import me.ddayo.aris.engine.client.ClientInGameEngine
import me.ddayo.aris.engine.client.ClientInitEngine
import me.ddayo.aris.engine.client.ClientMainEngine
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent
import net.neoforged.fml.loading.FMLEnvironment
import java.util.concurrent.CopyOnWriteArrayList

@Mod(Aris.MOD_ID)
class ArisNeoForge(modBus: IEventBus) {
    companion object {
        val initExtensions = CopyOnWriteArrayList<EngineInitializer<InitEngine>>()
        val inGameExtensions = CopyOnWriteArrayList<EngineInitializer<InGameEngine>>()
        // Keep the erased collections on both sides: @OnlyIn on Kotlin backing
        // fields removes them but leaves their writes in the static initializer.
        val clientInitExtensions = CopyOnWriteArrayList<EngineInitializer<ClientInitEngine>>()
        val clientMainExtensions = CopyOnWriteArrayList<EngineInitializer<ClientMainEngine>>()
        val clientInGameExtensions = CopyOnWriteArrayList<EngineInitializer<ClientInGameEngine>>()
        fun<T> cloneExtensions(ex: CopyOnWriteArrayList<EngineInitializer<T>>) where T: MCBaseEngine = mutableListOf<EngineInitializer<T>>().apply {
            ex.forEach { add(it) }
        }.toList()
    }

    init {
        modBus.addListener(RegistryHelperImpl::buildCreativeTabContents)
        // KotlinForForge auto-subscribes objects as instances, which rejects
        // @JvmStatic handlers. Explicit listeners preserve their public JVM API.
        modBus.addListener(ArisNeoForgeNetworking::register)
        if (FMLEnvironment.dist.isClient) {
            modBus.addListener(ArisNeoForgeClientInitEvents::registerParticleFactories)
        }
        modBus.addListener { it: FMLConstructModEvent ->
            it.enqueueWork {
                Aris.init()
                if (FMLEnvironment.dist.isClient)
                    ArisClient.init()
            }
        }
        modBus.addListener { it: FMLLoadCompleteEvent ->
            it.enqueueWork {
                CreativeTabRegistry.validateReferences()
                if (FMLEnvironment.dist.isClient)
                    ArisClient.onClientStart()
            }
        }

        RegistryHelperImpl.registries.forEach {
            it.register(modBus)
        }
    }
}
