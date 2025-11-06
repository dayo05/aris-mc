package me.ddayo.aris.forge

import me.ddayo.aris.Aris
import me.ddayo.aris.engine.hook.EntityHooks
import me.ddayo.aris.engine.wrapper.LuaItemStack
import me.ddayo.aris.engine.wrapper.LuaServerPlayer
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.event.server.ServerStartingEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = "aris")
object ArisForgeServerEventSubscriber {
    @SubscribeEvent
    fun onServerStarting(event: ServerStartingEvent) {
        // Initialize ARIS server
        Aris.onServerStart(event.server)
    }

    @SubscribeEvent
    fun onItemUse(event: PlayerInteractEvent.RightClickItem) {
        val player = event.entity as? ServerPlayer ?: return
        val stack = player.getItemInHand(event.hand)
        val sp = LuaServerPlayer(player as ServerPlayer)
        val lis = LuaItemStack(stack)
        EntityHooks.itemUseHook[BuiltInRegistries.ITEM.getKey(stack.item).toString()].callAsTask(sp, lis)
    }

    @SubscribeEvent
    fun onRegisterCommands(event: RegisterCommandsEvent) {
        Aris.registerCommand(event.dispatcher, event.buildContext)
    }

    @SubscribeEvent
    fun onServerTick(event: TickEvent.LevelTickEvent) {
        if (event.phase != TickEvent.Phase.END || event.side != LogicalSide.SERVER) return
        Aris.onServerTick()
    }
}
