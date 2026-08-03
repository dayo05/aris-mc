package me.ddayo.aris.engine

import dev.architectury.injectables.annotations.ExpectPlatform
import me.ddayo.aris.Aris
import me.ddayo.aris.RegistryHelper
import me.ddayo.aris.engine.item.ScriptableItem
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import net.minecraft.core.Registry
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item

@LuaProvider(InitEngine.PROVIDER, library = "aris.init")
object InitFunction {
    /**
     * 새로운 아이템을 추가합니다.
     * @param key 추가할 아이템 id
     */
    @LuaFunction("create_item")
    fun registerItem(key: String) {
        val location = RegistryHelper.getResourceLocation(key)
        val itemKey = ResourceKey.create(Registries.ITEM, location)

        RegistryHelper.registerItem(location) {
            ScriptableItem(
                location,
                Item.Properties().setId(itemKey)
            )
        }
    }

    @LuaFunction("create_particle")
    fun createParticle(key: String) {
        RegistryHelper.registerParticle(RegistryHelper.getResourceLocation(key))
    }
}
