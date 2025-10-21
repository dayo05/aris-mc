package me.ddayo.aris.forge

import me.ddayo.aris.Aris
import me.ddayo.aris.client.ArisClient
import net.minecraftforge.eventbus.api.Event
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent
import net.minecraftforge.fml.loading.FMLEnvironment
import org.apache.logging.log4j.LogManager
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.forge.MOD_CONTEXT

@net.minecraftforge.fml.common.Mod(Aris.MOD_ID)
class ArisForge {
    // TODO
    class AEvent: Event() {

    }
    @SubscribeEvent
    fun regA(e: AEvent) {
        LogManager.getLogger().info("test")
    }

    init {
        MOD_CONTEXT.getKEventBus().post(AEvent())
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
