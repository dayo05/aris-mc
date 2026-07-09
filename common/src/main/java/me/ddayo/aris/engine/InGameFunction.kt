package me.ddayo.aris.engine

import me.ddayo.aris.Aris
import me.ddayo.aris.RegistryHelper
import me.ddayo.aris.engine.wrapper.LuaEntity.Companion.toLuaValue
import me.ddayo.aris.engine.wrapper.LuaEntity
import me.ddayo.aris.engine.wrapper.LuaEntityType
import me.ddayo.aris.engine.wrapper.LuaItemStack
import me.ddayo.aris.engine.wrapper.LuaMobEffectInstance
import me.ddayo.aris.engine.wrapper.LuaServerWorld
import me.ddayo.aris.luagen.CoroutineProvider
import me.ddayo.aris.luagen.LuaCallback
import me.ddayo.aris.luagen.LuaCallbackParam
import me.ddayo.aris.luagen.LuaFunc
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import me.ddayo.aris.math.Point3
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.commands.CommandSourceStack
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.item.ItemStack


@LuaProvider(InGameEngine.PROVIDER, library = "aris.game")
object InGameFunction : CoroutineProvider {
    /**
     * 서버 콘솔에서 커멘드를 실행합니다.
     * @param command 실행할 명령어
     */
    @LuaFunction("dispatch_command")
    fun dispatchCommand(command: String) {
        val server = Aris.server
        val dispatcher = server.commands.dispatcher
        val results = dispatcher.parse(command, server.createCommandSourceStack().withSuppressedOutput())
        server.commands.performCommand(results, command)
    }

    /**
     * 마인크래프트 selector 문자열로 엔티티를 찾아 순회합니다.
     * 기본 실행 주체는 서버 콘솔입니다. @s 또는 상대 좌표가 필요한 경우 iter_selector_from을 사용하세요.
     * @param selector selector 문자열 (예: "@e[type=minecraft:zombie,limit=5]")
     * @param fn 각 엔티티에 대해 실행할 콜백
     */
    @LuaFunction("iter_selector")
    fun iterSelector(
        selector: String,
        @LuaCallback(params = [LuaCallbackParam("entity", LuaEntity::class)])
        fn: LuaFunc
    ) = iterSelectorWithSource(Aris.server.createCommandSourceStack().withSuppressedOutput(), selector, fn)

    /**
     * 특정 엔티티를 실행 주체로 사용해 마인크래프트 selector 문자열로 엔티티를 찾아 순회합니다.
     * @param source selector의 @s, 상대 좌표, 거리 조건 기준이 되는 엔티티
     * @param selector selector 문자열 (예: "@e[distance=..10]")
     * @param fn 각 엔티티에 대해 실행할 콜백
     */
    @LuaFunction("iter_selector_from")
    fun iterSelectorFrom(
        source: LuaEntity,
        selector: String,
        @LuaCallback(params = [LuaCallbackParam("entity", LuaEntity::class)])
        fn: LuaFunc
    ) = iterSelectorWithSource(MinecraftSelector.sourceStackFor(source.inner), selector, fn)

    private fun iterSelectorWithSource(source: CommandSourceStack, selector: String, fn: LuaFunc) = coroutine<Unit> {
        MinecraftSelector.findEntities(source, selector).forEach {
            yield(fn.await(this@coroutine, it.toLuaValue()))
        }
    }

    /**
     * 마인크래프트 selector 문자열의 첫 번째 엔티티를 가져옵니다.
     * 결과가 없으면 null을 반환합니다.
     * @param selector selector 문자열 (예: "@p")
     */
    @LuaFunction("get_selector_one")
    fun getSelectorOne(selector: String): LuaEntity? =
        MinecraftSelector.findEntities(Aris.server.createCommandSourceStack().withSuppressedOutput(), selector).firstOrNull()?.toLuaValue()

    /**
     * 특정 엔티티를 실행 주체로 사용해 selector 문자열의 첫 번째 엔티티를 가져옵니다.
     * 결과가 없으면 null을 반환합니다.
     * @param source selector의 @s, 상대 좌표, 거리 조건 기준이 되는 엔티티
     * @param selector selector 문자열 (예: "@s")
     */
    @LuaFunction("get_selector_one_from")
    fun getSelectorOneFrom(source: LuaEntity, selector: String): LuaEntity? =
        MinecraftSelector.findEntities(MinecraftSelector.sourceStackFor(source.inner), selector).firstOrNull()?.toLuaValue()

    @LuaFunction("create_effect_builder")
    fun createEffectBuilder(of: String) = LuaMobEffectInstance(ResourceLocation.parse(of))

    @LuaFunction("create_effect_builder")
    fun createEffectBuilder(ns: String, of: String) = LuaMobEffectInstance(ResourceLocation.tryBuild(ns, of)!!)

    @LuaFunction("summon_entity")
    fun summonEntityAt(entityType: LuaEntityType<*>, world: LuaServerWorld, pos: Point3): LuaEntity {
        val blockPos = BlockPos.containing(pos.x, pos.y, pos.z)

        val entity = entityType.inner.create(
            world.inner,
            { e ->
                e.moveTo(pos.x, pos.y, pos.z, 0f, 0f)
            },
            blockPos,
            EntitySpawnReason.COMMAND,
            true,
            false
        )!!

        world.inner.tryAddFreshEntityWithPassengers(entity)
        return LuaEntity(entity)
    }

    @LuaFunction("entity_type_of")
    fun getEntityTypeOf(str: String): LuaEntityType<Entity>? =
        RegistryHelper.getEntityType<Entity>(ResourceLocation.parse(str))?.let { LuaEntityType(it) }

    /**
     * 아이템 ID로 ItemStack을 생성합니다.
     * @param id 아이템 ID (예: "minecraft:diamond")
     * @param count 수량
     */
    @LuaFunction("create_item")
    fun createItem(id: String, count: Int) =
        LuaItemStack(ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(id)).get(), count))

    /**
     * 아이템 ID로 ItemStack을 생성합니다. (수량 1)
     * @param id 아이템 ID (예: "minecraft:diamond")
     */
    @LuaFunction("create_item")
    fun createItem(id: String) = createItem(id, 1)
}
