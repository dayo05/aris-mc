package me.ddayo.aris.engine.item

import me.ddayo.aris.RegistryHelper
import me.ddayo.aris.engine.InitEngine
import me.ddayo.aris.lua.glue.InitGenerated
import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import net.minecraft.resources.ResourceLocation

@LuaProvider(InitEngine.PROVIDER)
class CreativeTabAttrBuilder : ILuaStaticDecl by InitGenerated.CreativeTabAttrBuilder_LuaGenerated {
    private var title: String? = null
    private var iconItemId: ResourceLocation? = null

    /** 탭 이름을 일반 문자열로 지정합니다. 빈 이름은 허용하지 않습니다. */
    @LuaFunction("title")
    fun title(value: String) {
        require(value.isNotBlank()) { "Creative tab title must not be blank" }
        title = value
    }

    /** aris 아이템을 아이콘으로 지정합니다. 아이템은 이 선언 이후에 등록해도 됩니다. */
    @LuaFunction("icon")
    fun icon(path: String) {
        iconItemId = RegistryHelper.getResourceLocation(path)
    }

    /** 지정한 namespace의 아이템을 아이콘으로 지정합니다. 예: icon("minecraft", "diamond"). */
    @LuaFunction("icon")
    fun icon(namespace: String, path: String) {
        iconItemId = RegistryHelper.getResourceLocation(namespace, path)
    }

    internal fun snapshot(tabId: ResourceLocation) = CreativeTabConfig(
        requireNotNull(title) { "Creative tab $tabId: title is required" },
        requireNotNull(iconItemId) { "Creative tab $tabId: icon is required" },
    )
}
