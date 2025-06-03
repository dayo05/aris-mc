package me.ddayo.aris.forge

import me.ddayo.aris.Aris
import me.ddayo.aris.client.ArisClient
import me.ddayo.aris.engine.EngineInitializer
import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.engine.InitEngine
import me.ddayo.aris.engine.MCBaseEngine
import me.ddayo.aris.engine.client.ClientEngineAddOn
import me.ddayo.aris.engine.client.ClientInGameEngine
import me.ddayo.aris.engine.client.ClientInitEngine
import me.ddayo.aris.engine.client.ClientMainEngine
import me.ddayo.aris.engine.client.forge.ClientEngineAddOnImpl
import me.ddayo.aris.engine.forge.EngineAddOnImpl
import me.ddayo.aris.engine.forge.InitFunctionImpl
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.eventbus.api.Event
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.IModBusEvent
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent
import net.minecraftforge.fml.loading.FMLEnvironment
import org.apache.logging.log4j.LogManager
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.forge.MOD_CONTEXT

@net.minecraftforge.fml.common.Mod(Aris.MOD_ID)
class ArisForge {
    abstract class RegisterEngineEvent<T>(private val initializers: MutableList<EngineInitializer<T>>): Event(), IModBusEvent where T: MCBaseEngine {
        fun register(target: EngineInitializer<T>) {
            initializers.add(target)
        }
    }

    class RegisterInitEngineEvent(initializers: MutableList<EngineInitializer<InitEngine>>)
        : RegisterEngineEvent<InitEngine>(initializers)

    class RegisterInGameEngineEvent(initializers: MutableList<EngineInitializer<InGameEngine>>)
        : RegisterEngineEvent<InGameEngine>(initializers)

    @OnlyIn(Dist.CLIENT)
    class RegisterClientInitEngineEvent(initializers: MutableList<EngineInitializer<ClientInitEngine>>)
        : RegisterEngineEvent<ClientInitEngine>(initializers)

    @OnlyIn(Dist.CLIENT)
    class RegisterClientMainEngineEvent(initializers: MutableList<EngineInitializer<ClientMainEngine>>)
        : RegisterEngineEvent<ClientMainEngine>(initializers)

    @OnlyIn(Dist.CLIENT)
    class RegisterClientInGameEngineEvent(initializers: MutableList<EngineInitializer<ClientInGameEngine>>)
        : RegisterEngineEvent<ClientInGameEngine>(initializers)

    init {
        MOD_BUS.addListener { it: FMLConstructModEvent ->
            it.enqueueWork {
                MOD_BUS.post(RegisterInitEngineEvent(EngineAddOnImpl.initEngineInitializers))
                MOD_BUS.post(RegisterInGameEngineEvent(EngineAddOnImpl.inGameEngineInitializers))
                Aris.init()

                if (FMLEnvironment.dist.isClient) {
                    MOD_BUS.post(RegisterClientInitEngineEvent(ClientEngineAddOnImpl.clientInitEngineInitializers))
                    MOD_BUS.post(RegisterClientMainEngineEvent(ClientEngineAddOnImpl.clientMainEngineInitializers))
                    MOD_BUS.post(RegisterClientInGameEngineEvent(ClientEngineAddOnImpl.clientInGameEngineInitializers))
                    ArisClient.init()
                }
            }
        }
        MOD_BUS.addListener { it: FMLLoadCompleteEvent ->
            it.enqueueWork {
                if (FMLEnvironment.dist.isClient)
                    ArisClient.onClientStart()
            }
        }


        InitFunctionImpl.ITEMS.register(MOD_BUS)
        ArisForgeNetworking.register()
    }
}
