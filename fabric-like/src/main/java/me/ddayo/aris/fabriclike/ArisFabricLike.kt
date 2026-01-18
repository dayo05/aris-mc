package me.ddayo.aris.fabriclike

import me.ddayo.aris.Aris
import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.engine.hook.EntityHooks
import me.ddayo.aris.engine.hook.EntityHooks.executeOnEntityGotDamage
import me.ddayo.aris.engine.wrapper.LuaItemStack
import me.ddayo.aris.engine.wrapper.LuaPlayerEntity
import me.ddayo.aris.engine.wrapper.LuaServerPlayer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResultHolder

object ArisFabricLike {
    fun init() {
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
            InteractionResultHolder.pass(stack)
        }

        CommandRegistrationCallback.EVENT.register { dispatcher, registry, _ ->
            Aris.registerCommand(dispatcher, registry)
        }
        ServerNetworking.register()
    }
}
