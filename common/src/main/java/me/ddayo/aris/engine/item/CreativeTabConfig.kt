package me.ddayo.aris.engine.item

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack

/** Captured settings, independent of the init Lua engine and its builders. */
data class CreativeTabConfig(val title: String, val iconItemId: ResourceLocation) {
    fun build(key: ResourceLocation, builder: CreativeModeTab.Builder): CreativeModeTab = builder
        .title(Component.literal(title))
        .icon { ItemStack(CreativeTabRegistry.resolveItem(iconItemId, "Creative tab $key icon")) }
        .build()
}
