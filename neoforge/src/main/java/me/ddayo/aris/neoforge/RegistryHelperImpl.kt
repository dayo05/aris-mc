package me.ddayo.aris.neoforge

import me.ddayo.aris.Aris
import me.ddayo.aris.engine.item.CreativeTabConfig
import me.ddayo.aris.engine.item.CreativeTabRegistry
import net.minecraft.core.particles.ParticleType
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import net.neoforged.neoforge.registries.DeferredRegister

object RegistryHelperImpl {
    val ITEMS: DeferredRegister<Item> = DeferredRegister.create(BuiltInRegistries.ITEM, Aris.MOD_ID)
    val PARTICLES: DeferredRegister<ParticleType<*>> = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, Aris.MOD_ID)

    val CREATIVE_TABS: DeferredRegister<CreativeModeTab> = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, Aris.MOD_ID)

    val registries = listOf(ITEMS, PARTICLES, CREATIVE_TABS)

    @JvmStatic
    fun registerCreativeTab(key: ResourceLocation, config: CreativeTabConfig) {
        require(!BuiltInRegistries.CREATIVE_MODE_TAB.containsKey(key)) { "Creative tab is already registered: $key" }
        CREATIVE_TABS.register(key.path) { -> config.build(key, CreativeModeTab.builder()) }
        CreativeTabRegistry.recordTab(key, config)
    }

    @JvmStatic
    fun addItemToCreativeTab(tab: ResourceLocation, item: ResourceLocation) {
        CreativeTabRegistry.recordItem(tab, item)
    }

    fun buildCreativeTabContents(event: BuildCreativeModeTabContentsEvent) {
        val tabId = event.tabKey.location()
        CreativeTabRegistry.items(tabId).forEach { id ->
            val stack = ItemStack(CreativeTabRegistry.resolveItem(id, "Creative tab $tabId"))
            if (stack.item.isEnabled(event.flags)) {
                // Other mods may have already added this stack to either collection.
                if (event.parentEntries.none { ItemStack.isSameItemSameComponents(it, stack) }) {
                    event.accept(stack, CreativeModeTab.TabVisibility.PARENT_TAB_ONLY)
                }
                if (event.searchEntries.none { ItemStack.isSameItemSameComponents(it, stack) }) {
                    event.accept(stack, CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY)
                }
            }
        }
    }

    @JvmStatic
    fun registerItem(location: ResourceLocation, item: () -> Item) {
        ITEMS.register(location.path) { -> item() }
    }

    @JvmStatic
    fun registerParticle(location: ResourceLocation) {
        PARTICLES.register(location.path) { -> SimpleParticleType(false) }
    }

    @JvmStatic
    fun<T: Entity> getEntityType(rl: ResourceLocation): EntityType<T>? {
        return if (BuiltInRegistries.ENTITY_TYPE.containsKey(rl)) BuiltInRegistries.ENTITY_TYPE.get(rl).get().value() as? EntityType<T> else null
    }
}
