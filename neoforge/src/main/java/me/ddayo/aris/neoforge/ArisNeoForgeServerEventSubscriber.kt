package me.ddayo.aris.neoforge

import me.ddayo.aris.Aris
import me.ddayo.aris.engine.hook.EntityHooks
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.level.ServerPlayer
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent
import net.neoforged.neoforge.event.server.ServerStartingEvent
import net.neoforged.neoforge.event.tick.ServerTickEvent

@EventBusSubscriber(modid = "aris")
object ArisNeoForgeServerEventSubscriber {
    @SubscribeEvent
    fun onServerStarting(event: ServerStartingEvent) {
        Aris.onServerStart(event.server)
    }

    @SubscribeEvent
    fun onItemUse(event: PlayerInteractEvent.RightClickItem) {
        val player = event.entity as? ServerPlayer ?: return
        if (event.hand != net.minecraft.world.InteractionHand.MAIN_HAND) return
        val stack = player.getItemInHand(event.hand)
        EntityHooks.executeOnUseItem(BuiltInRegistries.ITEM.getKey(stack.item).toString(), player, stack)
        EntityHooks.executeOnRightClick(player)
    }

    @SubscribeEvent
    fun onRegisterCommands(event: RegisterCommandsEvent) {
        Aris.registerCommand(event.dispatcher, event.buildContext)
    }

    @SubscribeEvent
    fun onServerTick(event: ServerTickEvent.Post) {
        Aris.onServerTick()
    }
}
