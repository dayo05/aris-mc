package me.ddayo.aris.engine

import me.ddayo.aris.RegistryHelper
import me.ddayo.aris.engine.item.CreativeTabAttrBuilder
import me.ddayo.aris.engine.item.ItemAttrBuilder
import me.ddayo.aris.engine.item.ScriptableItem
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey

@LuaProvider(InitEngine.PROVIDER, library = "aris.init")
object InitFunction {
    /**
     * 새로운 아이템을 추가합니다.
     * @param key 추가할 아이템 id
     */
    @LuaFunction("create_item")
    fun registerItem(key: String) {
        registerItem(key, ItemAttrBuilder())
    }

    /** 빈 아이템 속성 빌더를 생성합니다. create_item 호출 시 설정이 복사됩니다. */
    @LuaFunction("item_attr")
    fun itemAttr() = ItemAttrBuilder()

    /**
     * 속성을 적용한 aris 아이템을 등록합니다. 등록 이후 빌더 수정은 이 아이템에 영향을 주지 않습니다.
     * @param key 추가할 아이템 경로 (namespace는 aris)
     * @param attr 아이템 속성 빌더
     */
    @LuaFunction("create_item")
    fun registerItem(key: String, attr: ItemAttrBuilder) {
        val location = RegistryHelper.getResourceLocation(key)
        val itemKey = ResourceKey.create(Registries.ITEM, location)
        val config = attr.snapshot(location)

        RegistryHelper.registerItem(location) {
            ScriptableItem(
                location,
                config.createProperties(itemKey)
            )
        }
        config.creativeTabs.forEach { RegistryHelper.addItemToCreativeTab(it, location) }
    }

    /** 빈 크리에이티브 탭 속성 빌더를 생성합니다. title과 icon은 필수입니다. */
    @LuaFunction("creative_tab_attr")
    fun creativeTabAttr() = CreativeTabAttrBuilder()

    /**
     * aris 크리에이티브 탭을 등록합니다. 변경에는 게임 재시작이 필요합니다.
     * @param key 탭 경로 (namespace는 aris)
     * @param attr 탭 속성 빌더. 호출 시 설정이 복사됩니다.
     */
    @LuaFunction("create_creative_tab")
    fun createCreativeTab(key: String, attr: CreativeTabAttrBuilder) {
        val location = RegistryHelper.getResourceLocation(key)
        RegistryHelper.registerCreativeTab(location, attr.snapshot(location))
    }

    @LuaFunction("create_particle")
    fun createParticle(key: String) {
        RegistryHelper.registerParticle(RegistryHelper.getResourceLocation(key))
    }
}
