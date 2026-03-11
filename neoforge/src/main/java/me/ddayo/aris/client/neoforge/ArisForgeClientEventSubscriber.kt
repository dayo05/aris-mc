package me.ddayo.aris.client.neoforge

import me.ddayo.aris.client.ArisClient
import me.ddayo.aris.client.gui.HudRenderer
import me.ddayo.aris.engine.client.ClientInGameEngine
import me.ddayo.aris.engine.client.ClientInitEngine
import me.ddayo.aris.neoforge.RegistryHelperImpl
import me.ddayo.aris.particle.CustomParticleProvider
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.core.particles.SimpleParticleType
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent


@EventBusSubscriber(modid = "aris", value = [Dist.CLIENT])
object ArisNeoForgeClientEvents {
    @SubscribeEvent
    fun onPlayerLoggedIn(evt: ClientPlayerNetworkEvent.LoggingIn) {
        ArisClient.onClientJoinGame()
    }

    @SubscribeEvent
    fun onPlayerLoggedOut(evt: ClientPlayerNetworkEvent.LoggingOut) {
        ArisClient.onClientLeaveGame()
    }

    @SubscribeEvent
    fun onClientTick(evt: ClientTickEvent.Post) {
        ArisClient.clientTick()
        val mc = Minecraft.getInstance()
        if (mc.player != null && mc.level != null) {
            ArisClient.clientWorldTick()
        }
    }

    @SubscribeEvent
    fun onRenderGuiOverlay(event: RenderGuiLayerEvent.Post) {
        val graphics: GuiGraphics = event.guiGraphics
        val delta = event.partialTick.getRealtimeDeltaTicks()
        HudRenderer.renderHud(graphics, delta)
    }
}

@EventBusSubscriber(modid = "aris", value = [Dist.CLIENT])
object ArisNeoForgeClientInitEvents {
    @SubscribeEvent
    @JvmStatic
    fun registerParticleFactories(event: RegisterParticleProvidersEvent) {
        RegistryHelperImpl.PARTICLES.entries.forEach { reg ->
            event.registerSpriteSet(reg.get() as SimpleParticleType) {
                return@registerSpriteSet CustomParticleProvider(it, ClientInitEngine.INSTANCE!!.particleInfo[reg.id] ?: throw IllegalStateException("Particle info not exists for particle ${reg.id}"))
            }
        }
    }
}
