package me.ddayo.aris.engine.item

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items

/** Startup declarations only; registry references are resolved after every mod has registered. */
object CreativeTabRegistry {
    private val tabs = linkedMapOf<ResourceLocation, CreativeTabConfig>()
    private val entries = linkedMapOf<ResourceLocation, LinkedHashSet<ResourceLocation>>()

    fun recordTab(id: ResourceLocation, config: CreativeTabConfig) {
        check(tabs.putIfAbsent(id, config) == null) { "Creative tab is already declared: $id" }
    }

    fun recordItem(tab: ResourceLocation, item: ResourceLocation): Boolean =
        entries.getOrPut(tab) { linkedSetOf() }.add(item)

    fun items(tab: ResourceLocation): List<ResourceLocation> = entries[tab]?.toList().orEmpty()

    fun resolveItem(id: ResourceLocation, context: String): Item {
        val item = BuiltInRegistries.ITEM.getOptional(id).orElse(null)
        require(item != null && item != Items.AIR) { "$context: unknown or empty item $id" }
        return item
    }

    /** Called at client/server startup on Fabric, and load-complete on NeoForge. */
    fun validateReferences() {
        tabs.forEach { (id, config) -> resolveItem(config.iconItemId, "Creative tab $id icon") }
        entries.forEach { (tabId, itemIds) ->
            val tab = BuiltInRegistries.CREATIVE_MODE_TAB.getOptional(tabId).orElse(null)
            require(tab != null) { "Unknown creative tab $tabId referenced by items ${itemIds.joinToString()}" }
            require(tab.type == CreativeModeTab.Type.CATEGORY) {
                "Creative tab $tabId is not a category (items ${itemIds.joinToString()}); use a category to include items in search"
            }
            itemIds.forEach { resolveItem(it, "Creative tab $tabId") }
        }
    }
}
