package me.ddayo.aris.engine.item

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item

class ScriptableItem(private val id: ResourceLocation, property: Properties) : Item(property) {
    /*
    override fun use(
        level: Level,
        player: Player,
        interactionHand: InteractionHand
    ): InteractionResultHolder<ItemStack> {
        if (level.isClientSide) return InteractionResultHolder.pass(player.getItemInHand(interactionHand))

        InGameEngine.INSTANCE?.itemUseHook?.let {
            it[id.path]?.mutableForEach {
                it.call(
                    LuaServerPlayer(player as ServerPlayer),
                    LuaItemStack(player.getItemInHand(interactionHand))
                )
            }
        }

        return InteractionResultHolder.success(player.getItemInHand(interactionHand))
    }
     */
}