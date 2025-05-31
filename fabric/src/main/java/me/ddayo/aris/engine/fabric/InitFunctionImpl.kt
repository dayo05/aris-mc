package me.ddayo.aris.engine.fabric

import me.ddayo.aris.engine.InitEngine
import me.ddayo.aris.luagen.LuaProvider
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item

@LuaProvider(InitEngine.Companion.PROVIDER, library = "aris.init")
object InitFunctionImpl {
    @JvmStatic
    fun registerItem(location: ResourceLocation, item: () -> Item) {
        Registry.register(
            BuiltInRegistries.ITEM,
            location,
            item()
        )
    }
}