package me.ddayo.aris.engine

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.exceptions.CommandSyntaxException
import me.ddayo.aris.Aris
import net.minecraft.commands.CommandSource
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.commands.arguments.selector.EntitySelector
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec2

object MinecraftSelector {
    fun parseEntities(selector: String): EntitySelector {
        val reader = StringReader(selector)
        val parsed = EntityArgument.entities().parse(reader)
        if (reader.canRead()) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(reader)
        }
        return parsed
    }

    fun findEntities(source: CommandSourceStack, selector: String): List<Entity> =
        parseEntities(selector).findEntities(source)

    fun sourceStackFor(entity: Entity): CommandSourceStack =
        CommandSourceStack(
            CommandSource.NULL,
            entity.position(),
            Vec2(entity.xRot, entity.yRot),
            entity.level() as ServerLevel,
            2,
            entity.name.string,
            entity.displayName ?: Component.literal(entity.name.string),
            Aris.server,
            entity
        ).withSuppressedOutput()
}
