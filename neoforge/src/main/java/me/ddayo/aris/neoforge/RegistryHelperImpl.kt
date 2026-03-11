package me.ddayo.aris.neoforge

import me.ddayo.aris.Aris
import net.minecraft.core.particles.ParticleType
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.neoforged.neoforge.registries.DeferredRegister

object RegistryHelperImpl {
    val ITEMS: DeferredRegister<Item> = DeferredRegister.create(BuiltInRegistries.ITEM, Aris.MOD_ID)
    val PARTICLES: DeferredRegister<ParticleType<*>> = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, Aris.MOD_ID)

    val registries = listOf(ITEMS, PARTICLES)

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
