package me.ddayo.aris.engine

import dev.architectury.injectables.annotations.ExpectPlatform
import me.ddayo.aris.Aris
import me.ddayo.aris.engine.item.ScriptableItem
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
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
        registerItem(ResourceLocation(Aris.MOD_ID, key)) {
            ScriptableItem(
                ResourceLocation(Aris.MOD_ID, key),
                Item.Properties()
            )
        }
    }

    @JvmStatic
    @ExpectPlatform
    fun registerItem(location: ResourceLocation, item: () -> Item) {
        throw NotImplementedError()
    }
}