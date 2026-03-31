package me.ddayo.aris.engine

import me.ddayo.aris.Aris
import me.ddayo.aris.RegistryHelper
import me.ddayo.aris.engine.wrapper.LuaEntity
import me.ddayo.aris.engine.wrapper.LuaEntityType
import me.ddayo.aris.engine.wrapper.LuaMobEffectInstance
import me.ddayo.aris.engine.wrapper.LuaServerWorld
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import me.ddayo.aris.math.Point3
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntitySpawnReason


@LuaProvider(InGameEngine.PROVIDER, library = "aris.game")
object InGameFunction {
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

        return LuaEntity(entity)
    }

    @LuaFunction("entity_type_of")
    fun getEntityTypeOf(str: String): LuaEntityType<Entity>? =
        RegistryHelper.getEntityType<Entity>(ResourceLocation.parse(str))?.let { LuaEntityType(it) }
}