package me.ddayo.aris.fabric

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item

object RegistryHelperImpl {
    @JvmStatic
    fun registerItem(location: ResourceLocation, item: () -> Item) {
        Registry.register(
            BuiltInRegistries.ITEM,
            location,
            item()
        )
    }

    @JvmStatic
    fun<T: Entity> getEntityType(rl: ResourceLocation): EntityType<T>? {
        return if (BuiltInRegistries.ENTITY_TYPE.containsKey(rl)) BuiltInRegistries.ENTITY_TYPE.get(rl) as? EntityType<T> else null
    }
}