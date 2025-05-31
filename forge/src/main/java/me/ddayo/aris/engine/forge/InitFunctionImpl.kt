package me.ddayo.aris.engine.forge

import me.ddayo.aris.Aris
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries


object InitFunctionImpl {
    val ITEMS: DeferredRegister<Item> = DeferredRegister.create<Item>(ForgeRegistries.ITEMS, Aris.MOD_ID)

    @JvmStatic
    fun registerItem(location: ResourceLocation, item: () -> Item) {
        ITEMS.register(location.path, item)
    }
}