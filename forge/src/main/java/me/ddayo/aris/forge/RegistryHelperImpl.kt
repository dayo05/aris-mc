package me.ddayo.aris.forge

import me.ddayo.aris.Aris
import net.minecraft.core.particles.ParticleType
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

object RegistryHelperImpl {
    val ITEMS: DeferredRegister<Item> = DeferredRegister.create<Item>(ForgeRegistries.ITEMS, Aris.MOD_ID)
    val PARTICLES: DeferredRegister<ParticleType<*>> = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Aris.MOD_ID)

    val registries = listOf(ITEMS, PARTICLES)
    @JvmStatic
    fun registerItem(location: ResourceLocation, item: () -> Item) {
        ITEMS.register(location.path, item)
    }

    @JvmStatic
    fun registerParticle(location: ResourceLocation) {
        PARTICLES.register(location.path) { SimpleParticleType(false) }
    }

    @JvmStatic
    fun<T: Entity> getEntityType(rl: ResourceLocation): EntityType<T>? {
        return ForgeRegistries.ENTITY_TYPES.getValue(rl) as? EntityType<T>
    }
}