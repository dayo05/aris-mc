package me.ddayo.aris.client.fabric

import me.ddayo.aris.RegistryHelper
import me.ddayo.aris.client.ArisClient
import me.ddayo.aris.client.gui.Resource
import me.ddayo.aris.engine.EngineInitializer
import me.ddayo.aris.engine.client.ClientInGameEngine
import me.ddayo.aris.engine.client.ClientInitEngine
import me.ddayo.aris.engine.client.ClientMainEngine
import me.ddayo.aris.particle.CustomParticleProvider
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.minecraft.server.packs.resources.ResourceManager
import org.apache.logging.log4j.LogManager
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

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

        ArisClient.init()
        ClientLifecycleEvents.CLIENT_STARTED.register {
            ArisClient.onClientStart()
        }

        ClientLifecycleEvents.CLIENT_STOPPING.register {
            ArisClient.onClientClose()
        }

        ClientPlayConnectionEvents.JOIN.register { handler, sender, client ->
            ArisClient.onClientJoinGame()
        }

        ClientPlayConnectionEvents.DISCONNECT.register { handler, client ->
            ArisClient.onClientLeaveGame()
        }

        ClientTickEvents.START_CLIENT_TICK.register {
            ArisClient.clientTick()
        }

        ClientTickEvents.START_WORLD_TICK.register {
            ArisClient.clientWorldTick()
        }

        HudLayerRegistrationCallback.EVENT.register {
            it.attachLayerAfter(IdentifiedLayer.EXPERIENCE_LEVEL, object: IdentifiedLayer {
                override fun id() = RegistryHelper.getResourceLocation("aris_main_hud")
                override fun render(
                    graphics: GuiGraphics,
                    delta: DeltaTracker
                ) {
                    ClientInGameEngine.INSTANCE?.renderHud(graphics, delta.gameTimeDeltaTicks)
                }
            })
        }

        ClientInitEngine.INSTANCE!!.particleInfo.forEach { (rl, info) ->
            ParticleFactoryRegistry.getInstance().register(
                BuiltInRegistries.PARTICLE_TYPE[rl].get().value() as SimpleParticleType,
            ) {
                CustomParticleProvider(it, info)
            }
        }

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES)
            .registerReloadListener(
                object: IdentifiableResourceReloadListener {
                    override fun getFabricId() = RegistryHelper.getResourceLocation("client_res_reload_handler")

                    override fun reload(
                        preparationBarrier: PreparableReloadListener.PreparationBarrier,
                        resourceManager: ResourceManager,
                        executor: Executor,
                        executor2: Executor
                    ): CompletableFuture<Void?> {
                        return preparationBarrier.wait(Unit).thenAcceptAsync({
                            Resource.clearResource()
                            ClientInGameEngine.reloadEngine()
                            ClientMainEngine.reloadEngine()
                        }, executor2)
                    }
                })

        ClientNetworkingFabric.register()
    }
}