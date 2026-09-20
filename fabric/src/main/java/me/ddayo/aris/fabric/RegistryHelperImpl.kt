package me.ddayo.aris.fabric

import me.ddayo.aris.engine.item.CreativeTabConfig
import me.ddayo.aris.engine.item.CreativeTabRegistry
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.TextureSheetParticle
import net.minecraft.core.Registry
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item

object RegistryHelperImpl {
    @JvmStatic
    fun registerCreativeTab(key: ResourceLocation, config: CreativeTabConfig) {
        require(!BuiltInRegistries.CREATIVE_MODE_TAB.containsKey(key)) { "Creative tab is already registered: $key" }
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, key, config.build(key, FabricItemGroup.builder()))
        CreativeTabRegistry.recordTab(key, config)
    }

    @JvmStatic
    fun addItemToCreativeTab(tab: ResourceLocation, item: ResourceLocation) {
        if (CreativeTabRegistry.recordItem(tab, item)) {
            ItemGroupEvents.modifyEntriesEvent(ResourceKey.create(Registries.CREATIVE_MODE_TAB, tab)).register { entries ->
                entries.accept(CreativeTabRegistry.resolveItem(item, "Creative tab $tab"))
            }
        }
    }

    @JvmStatic
    fun registerItem(location: ResourceLocation, item: () -> Item) {
        require(!BuiltInRegistries.ITEM.containsKey(location)) {
            "Item is already registered: $location"
        }

        Registry.register(
            BuiltInRegistries.ITEM,
            location,
            item()
        )
    }

    @JvmStatic
    fun registerParticle(key: ResourceLocation) {
        Registry.register(
            BuiltInRegistries.PARTICLE_TYPE,
            key,
            FabricParticleTypes.simple()
        )
    }

    @JvmStatic
    fun<T: Entity> getEntityType(rl: ResourceLocation): EntityType<T>? {
        return if (BuiltInRegistries.ENTITY_TYPE.containsKey(rl)) BuiltInRegistries.ENTITY_TYPE.get(rl).get().value() as? EntityType<T> else null
    }
}
