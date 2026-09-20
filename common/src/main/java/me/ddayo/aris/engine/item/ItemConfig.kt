package me.ddayo.aris.engine.item

import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item

internal data class ItemConfig(
    val maxStackSize: Int,
    val durability: Int?,
    val fireResistant: Boolean,
    val creativeTabs: List<ResourceLocation>,
) {
    fun createProperties(key: ResourceKey<Item>): Item.Properties = Item.Properties().setId(key).apply {
        stacksTo(maxStackSize)
        durability?.let { durability(it) }
        if (fireResistant) fireResistant()
    }
}
