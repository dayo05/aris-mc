package me.ddayo.aris.client.fabriclike

import me.ddayo.aris.client.ArisClient
import me.ddayo.aris.engine.client.ClientInGameEngine
import me.ddayo.aris.engine.client.ClientInitEngine
import me.ddayo.aris.particle.CustomParticleProvider
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.core.registries.BuiltInRegistries

object ArisFabricLikeClient {
    fun init() {
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

        HudRenderCallback.EVENT.register { graphics, delta ->
            ClientInGameEngine.INSTANCE?.renderHud(graphics, delta)
        }

        ClientInitEngine.INSTANCE!!.particleInfo.forEach { (rl, info) ->
            ParticleFactoryRegistry.getInstance().register(
                BuiltInRegistries.PARTICLE_TYPE[rl] as SimpleParticleType,
            ) {
                CustomParticleProvider(it, info)
            }
        }

        ClientNetworking.register()
    }
}