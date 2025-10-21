package me.ddayo.aris

import dev.architectury.injectables.annotations.ExpectPlatform
import net.minecraft.client.particle.TextureSheetParticle
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item

object RegistryHelper {
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