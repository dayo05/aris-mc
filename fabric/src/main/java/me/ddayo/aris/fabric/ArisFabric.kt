package me.ddayo.aris.fabric

import me.ddayo.aris.Aris
import me.ddayo.aris.engine.EngineInitializer
import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.engine.InitEngine
import me.ddayo.aris.engine.hook.EntityHooks
import me.ddayo.aris.engine.wrapper.LuaItemStack
import me.ddayo.aris.engine.wrapper.LuaPlayerEntity
import me.ddayo.aris.engine.wrapper.LuaServerPlayer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult
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

        Aris.init()
        ServerLifecycleEvents.SERVER_STARTING.register { server ->
            Aris.onServerStart(server)
        }
        ServerTickEvents.START_SERVER_TICK.register {
            Aris.onServerTick()
        }

        UseItemCallback.EVENT.register { player, world, hand ->
            val stack = player.getItemInHand(hand)
            InGameEngine.INSTANCE?.let {
                if (!world.isClientSide) {
                    val sp = LuaServerPlayer(player as ServerPlayer)
                    val lis = LuaItemStack(stack)
                    EntityHooks.itemUseHook[BuiltInRegistries.ITEM.getKey(stack.item).toString()].callAsTask(sp, lis)

                    EntityHooks.rightClickHook.callAsTask(LuaPlayerEntity(player))
                }
            }

            InteractionResult.PASS
        }

        CommandRegistrationCallback.EVENT.register { dispatcher, registry, _ ->
            Aris.registerCommand(dispatcher, registry)
        }

        ServerNetworkingFabric.init()
    }
}