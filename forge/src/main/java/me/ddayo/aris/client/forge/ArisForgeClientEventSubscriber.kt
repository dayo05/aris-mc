package me.ddayo.aris.client.forge

import me.ddayo.aris.client.ArisClient
import me.ddayo.aris.engine.client.ClientInGameEngine
import me.ddayo.aris.engine.client.ClientInitEngine
import me.ddayo.aris.forge.RegistryHelperImpl
import me.ddayo.aris.particle.CustomParticleProvider
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.core.particles.SimpleParticleType
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.ClientPlayerNetworkEvent
import net.minecraftforge.client.event.RegisterParticleProvidersEvent
import net.minecraftforge.client.event.RenderGuiOverlayEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod


@Mod.EventBusSubscriber(modid = "aris", bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
object ArisForgeClientEvents {
    /** Fired once when the client has fully logged into a world. */
    @SubscribeEvent
    fun onPlayerLoggedIn(evt: ClientPlayerNetworkEvent.LoggingIn) {
        ArisClient.onClientJoinGame()
    }

    /** Fired once when the client logs out of a world (or quits to menu). */
    @SubscribeEvent
    fun onPlayerLoggedOut(evt: ClientPlayerNetworkEvent.LoggingOut) {
        ArisClient.onClientLeaveGame()
    }

    /** Your per-frame client tick logic. */
    @SubscribeEvent
    fun onClientTick(evt: TickEvent.ClientTickEvent) {
        if (evt.phase == TickEvent.Phase.END) {
            ArisClient.clientTick()
            val mc = Minecraft.getInstance()
            if (mc.player != null && mc.level != null) {
                ArisClient.clientWorldTick()
            }
        }
    }

    @SubscribeEvent
    fun onRenderGuiOverlay(event: RenderGuiOverlayEvent.Post) {
        val graphics: GuiGraphics = event.guiGraphics
        val delta = event.partialTick
        ClientInGameEngine.INSTANCE?.renderHud(graphics, delta)
    }
}

@Mod.EventBusSubscriber(modid = "aris", bus = Mod.EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
class ArisForgeClientInitEvents {
    @SubscribeEvent
    fun registerParticleFactories(event: RegisterParticleProvidersEvent) {
        RegistryHelperImpl.PARTICLES.entries.forEach { reg ->
            event.registerSpriteSet(reg.get() as SimpleParticleType) {
                return@registerSpriteSet CustomParticleProvider(it, ClientInitEngine.INSTANCE!!.particleInfo[reg.id] ?: throw IllegalStateException("Particle info not exists for particle ${reg.id}"))
            }
        }
    }
}
