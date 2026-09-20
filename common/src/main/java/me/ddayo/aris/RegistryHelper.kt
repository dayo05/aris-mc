package me.ddayo.aris

import dev.architectury.injectables.annotations.ExpectPlatform
import me.ddayo.aris.engine.item.CreativeTabConfig
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item

object RegistryHelper {
    fun getResourceLocation(of: String): ResourceLocation = getResourceLocation(Aris.MOD_ID, of)

    fun getResourceLocation(namespace: String, path: String): ResourceLocation =
        ResourceLocation.fromNamespaceAndPath(namespace, path)

    @JvmStatic
    @ExpectPlatform
    fun registerCreativeTab(key: ResourceLocation, config: CreativeTabConfig) {
        throw NotImplementedError()
    }

    @JvmStatic
    @ExpectPlatform
    fun addItemToCreativeTab(tab: ResourceLocation, item: ResourceLocation) {
        throw NotImplementedError()
    }

    @JvmStatic
    @ExpectPlatform
    fun registerItem(key: ResourceLocation, item: () -> Item) {
        throw NotImplementedError()
    }

    @JvmStatic
    @ExpectPlatform
    fun registerParticle(key: ResourceLocation) {
        throw NotImplementedError()
    }

    @JvmStatic
    @ExpectPlatform
    fun<T: Entity> getEntityType(key: ResourceLocation): EntityType<T>? {
        throw NotImplementedError()
    }
}
