package me.ddayo.aris.engine.item

import me.ddayo.aris.RegistryHelper
import me.ddayo.aris.engine.InitEngine
import me.ddayo.aris.lua.glue.InitGenerated
import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import net.minecraft.resources.ResourceLocation

/** Mutable init-only settings. Each create_item call captures a separate snapshot. */
@LuaProvider(InitEngine.PROVIDER)
class ItemAttrBuilder : ILuaStaticDecl by InitGenerated.ItemAttrBuilder_LuaGenerated {
    private var maxStackSize: Int? = null
    private var durability: Int? = null
    private var fireResistant = false
    private val creativeTabs = linkedSetOf<ResourceLocation>()

    /** 최대 스택 수를 지정합니다 (1..99). 내구도가 있으면 1만 허용합니다. */
    @LuaFunction("max_stack_size")
    fun maxStackSize(value: Int) {
        require(value in 1..99) { "max_stack_size must be between 1 and 99: $value" }
        maxStackSize = value
    }

    /** 최대 내구도를 지정합니다 (양수). 스택 수를 생략했다면 1을 사용합니다. */
    @LuaFunction("durability")
    fun durability(value: Int) {
        require(value > 0) { "durability must be greater than zero: $value" }
        durability = value
    }

    /** 드롭된 아이템의 화염/용암 피해 저항 여부를 지정합니다. 기본값은 false입니다. */
    @LuaFunction("fire_resistant")
    fun fireResistant(value: Boolean) {
        fireResistant = value
    }

    /** aris namespace의 크리에이티브 탭에 추가합니다. 여러 번 호출할 수 있습니다. */
    @LuaFunction("creative_tab")
    fun creativeTab(path: String) {
        creativeTabs += RegistryHelper.getResourceLocation(path)
    }

    /** 지정한 namespace의 탭에 추가합니다. 예: creative_tab("minecraft", "combat"). */
    @LuaFunction("creative_tab")
    fun creativeTab(namespace: String, path: String) {
        creativeTabs += RegistryHelper.getResourceLocation(namespace, path)
    }

    internal fun snapshot(itemId: ResourceLocation): ItemConfig {
        require(durability == null || maxStackSize == null || maxStackSize == 1) {
            "Item $itemId: durability requires max_stack_size 1 (or omit max_stack_size)"
        }
        return ItemConfig(
            maxStackSize ?: if (durability != null) 1 else 64,
            durability,
            fireResistant,
            creativeTabs.toList(),
        )
    }
}
