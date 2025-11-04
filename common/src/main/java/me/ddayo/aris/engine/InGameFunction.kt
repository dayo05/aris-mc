package me.ddayo.aris.engine

import me.ddayo.aris.Aris
import me.ddayo.aris.RegistryHelper
import me.ddayo.aris.engine.hook.PlayerHooks
import me.ddayo.aris.engine.wrapper.LuaEntity
import me.ddayo.aris.engine.wrapper.LuaEntityType
import me.ddayo.aris.engine.wrapper.LuaMobEffectInstance
import me.ddayo.aris.engine.wrapper.LuaServerWorld
import me.ddayo.aris.luagen.LuaFunc
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import me.ddayo.aris.luagen.RetrieveEngine
import me.ddayo.aris.math.Point3
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import org.apache.logging.log4j.LogManager


@LuaProvider(InGameEngine.PROVIDER, library = "aris.game")
object InGameFunction {
    fun warnHookFn() = LogManager.getLogger().warn("Use aris.game.hook.* instead")
    /**
     * 추가한 아이템을 사용했을때 실행할 함수를 추가합니다.
     * @param item 아이템 id
     * @param func 실행할 함수
     */
    @LuaFunction("add_on_use_item")
    fun onUseItemHook(item: String, func: LuaFunc) {
        warnHookFn()
        PlayerHooks.onUseItemHook(item, func)
    }

    /**
     * add_on_use_item을 통해 등록한 함수들을 초기화합니다.
     * @param item 초기화할 아이템
     */
    @LuaFunction("clear_on_use_item")
    fun clearOnUseItem(item: String) {
        warnHookFn()
        PlayerHooks.clearOnUseItem(item)
    }

    /**
     * 플레이어가 임의의 위치를 우클릭시 실행할 함수
     * @param f 실행할 함수
     */
    @LuaFunction("add_on_right_click_hook")
    fun onRightClick(f: LuaFunc) {
        warnHookFn()
        PlayerHooks.rightClickHook.add(f)
    }

    /**
     * 매 틱마다 실행할 함수를 추가합니다.
     * @param f 실행할 함수
     */
    @LuaFunction("add_tick_hook")
    fun addTickHook(@RetrieveEngine engine: InGameEngine, f: LuaFunc) {
        warnHookFn()
        engine.tickHook.add(f)
    }

    /**
     * 서버 콘솔에서 커멘드를 실행합니다.
     * @param command 실행할 명령어
     */
    @LuaFunction("dispatch_command")
    fun dispatchCommand(command: String) {
        val server = Aris.server
        val dispatcher = server.commands.dispatcher
        val results = dispatcher.parse(command, server.createCommandSourceStack())
        server.commands.performCommand(results, command)
    }

    @LuaFunction("create_effect_builder")
    fun createEffectBuilder(of: String) = LuaMobEffectInstance(ResourceLocation(of))

    @LuaFunction("create_effect_builder")
    fun createEffectBuilder(ns: String, of: String) = LuaMobEffectInstance(ResourceLocation(ns, of))

    @LuaFunction("summon_entity")
    fun summonEntityAt(entityType: LuaEntityType<*>, world: LuaServerWorld, pos: Point3): LuaEntity {
        val entity = entityType.inner.create(world.inner)!!
        entity.moveTo(pos.x, pos.y, pos.z, 0.0f, 0.0f)
        world.inner.addFreshEntity(entity)
        return LuaEntity(entity)
    }

    @LuaFunction("entity_type_of")
    fun getEntityTypeOf(str: String): LuaEntityType<Entity>? =
        RegistryHelper.getEntityType<Entity>(ResourceLocation(str))?.let { LuaEntityType(it) }
}