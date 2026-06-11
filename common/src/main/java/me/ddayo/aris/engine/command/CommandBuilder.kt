package me.ddayo.aris.engine.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import me.ddayo.aris.RegistryHelper
import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.luagen.LuaFunc
import me.ddayo.aris.engine.InitEngine
import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.engine.hook.CommandHooks
import me.ddayo.aris.engine.wrapper.LuaServerPlayer
import me.ddayo.aris.lua.glue.InitGenerated
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.resources.ResourceLocation
import party.iroiro.luajava.value.LuaValue
import java.util.concurrent.CompletableFuture
import java.util.Locale


@LuaProvider(InitEngine.PROVIDER, library = "aris.init.command")
object CommandBuilderFunctions {
    /**
     * 하위 커멘드를 추가합니다.
     * @of 추가할 커멘드 이름
     * @return 여기에서 획득한 값을 커멘드 핸들러에 append해야합니다.
     */
    @LuaFunction("sub_command")
    fun subCommand(of: String) = object : AbstractCommandHandler() {
        override fun retrieve() = Commands.literal(of)
        override fun write(ctx: CommandContext<CommandSourceStack>, builder: CommandBuilder) { /* Nothing to write */
        }
    }

    /**
     * 정수 인수를 추가합니다.
     * @of 추가할 정수 인수 이름
     * @return 여기에서 획득한 값을 커멘드 핸들러에 append해야합니다.
     */
    @LuaFunction("integer_arg")
    fun intArg(of: String) = object : AbstractCommandHandler() {
        val rl = RegistryHelper.getResourceLocation(of)
        override fun retrieve() = Commands.argument(of, IntegerArgumentType.integer())
        override fun write(ctx: CommandContext<CommandSourceStack>, builder: CommandBuilder) {
            builder.inner[rl] = IntegerArgumentType.getInteger(ctx, of)
        }
    }

    /**
     * 실수 인수를 추가합니다.
     * @of 추가할 실수 인수 이름
     * @return 여기에서 획득한 값을 커멘드 핸들러에 append해야합니다.
     */
    @LuaFunction("float_arg")
    fun floatArg(of: String) = object : AbstractCommandHandler() {
        val rl = RegistryHelper.getResourceLocation(of)
        override fun retrieve() = Commands.argument(of, DoubleArgumentType.doubleArg())
        override fun write(ctx: CommandContext<CommandSourceStack>, builder: CommandBuilder) {
            builder.inner[rl] = DoubleArgumentType.getDouble(ctx, of)
        }
    }
    /**
     * 플레이어 인수를 추가합니다.
     * @of 추가할 플레이어 인수 이름
     * @return 여기에서 획득한 값을 커멘드 핸들러에 append해야합니다.
     */
    @LuaFunction("player_arg")
    fun playerArg(of: String) = object : AbstractCommandHandler() {
        val rl = RegistryHelper.getResourceLocation(of)
        override fun retrieve() = Commands.argument(of, EntityArgument.player())
        override fun write(ctx: CommandContext<CommandSourceStack>, builder: CommandBuilder) {
            builder.inner[rl] = LuaServerPlayer(EntityArgument.getPlayer(ctx, of))
        }
    }

    /**
     * 문자열 인수를 추가합니다.
     * @of 추가할 문자열 인수 이름
     * @return 여기에서 획득한 값을 커멘드 핸들러에 append해야합니다.
     */
    @LuaFunction("string_arg")
    fun stringArg(of: String) = object : AbstractCommandHandler() {
        val rl = RegistryHelper.getResourceLocation(of)
        override fun retrieve() = Commands.argument(of, StringArgumentType.string())
        override fun write(ctx: CommandContext<CommandSourceStack>, builder: CommandBuilder) {
            builder.inner[rl] = StringArgumentType.getString(ctx, of)
        }
    }

    /**
     * Word 인수를 추가합니다.
     * @of 추가할 word 인수 이름
     * @return 여기에서 획득한 값을 커멘드 핸들러에 append해야합니다.
     */
    @LuaFunction("word_arg")
    fun wordArg(of: String) = object : AbstractCommandHandler() {
        val rl = RegistryHelper.getResourceLocation(of)
        override fun retrieve() = Commands.argument(of, StringArgumentType.word())
        override fun write(ctx: CommandContext<CommandSourceStack>, builder: CommandBuilder) {
            builder.inner[rl] = StringArgumentType.getString(ctx, of)
        }
    }

    /**
     * 자동완성 리스트를 사용하는 문자열 인수를 추가합니다.
     * @param of 추가할 문자열 인수 이름
     * @param list 자동완성에 사용할 리스트 id
     * @return 여기에서 획득한 값을 커멘드 핸들러에 append해야합니다.
     */
    @LuaFunction("suggested_string_arg")
    fun suggestedStringArg(of: String, list: String) = stringArg(of).apply {
        setSuggestionList(list)
    }

    /**
     * 자동완성 리스트를 사용하는 word 인수를 추가합니다.
     * @param of 추가할 word 인수 이름
     * @param list 자동완성에 사용할 리스트 id
     * @return 여기에서 획득한 값을 커멘드 핸들러에 append해야합니다.
     */
    @LuaFunction("suggested_word_arg")
    fun suggestedWordArg(of: String, list: String) = wordArg(of).apply {
        setSuggestionList(list)
    }


    val commands = mutableMapOf<ResourceLocation, AbstractCommandHandler>()

    /**
     * 새로운 명령어를 추가합니다.
     * @of 추가할 명령어 이름
     */
    @LuaFunction("create_command")
    fun createCommand(of: String): AbstractCommandHandler {
        val r = object : AbstractCommandHandler() {
            override fun retrieve() = Commands.literal(of)
            override fun write(
                ctx: CommandContext<CommandSourceStack>,
                builder: CommandBuilder
            ) { /* Nothing to write */
            }
        }
        commands[RegistryHelper.getResourceLocation(of)] = r
        return r
    }

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        commands.values.forEach {
            dispatcher.register(it.build() as LiteralArgumentBuilder)
        }
    }
}

@LuaProvider(InitEngine.PROVIDER, library = "aris.init.command.suggestion")
@LuaProvider(InGameEngine.PROVIDER, library = "aris.game.command.suggestion")
object CommandSuggestionFunctions {
    /**
     * 명령어 자동완성 리스트를 설정합니다.
     * @param id 자동완성 리스트 id
     * @param values 자동완성 후보 문자열 배열(table)
     */
    @LuaFunction("set_list")
    fun setList(id: String, values: LuaValue) {
        CommandSuggestionLists.set(RegistryHelper.getResourceLocation(id), values)
    }

    /**
     * 명령어 자동완성 리스트를 비웁니다.
     * @param id 자동완성 리스트 id
     */
    @LuaFunction("clear_list")
    fun clearList(id: String) {
        CommandSuggestionLists.clear(RegistryHelper.getResourceLocation(id))
    }
}

object CommandSuggestionLists {
    private val lists = mutableMapOf<ResourceLocation, List<String>>()

    @Synchronized
    fun set(id: ResourceLocation, values: LuaValue) {
        lists[id] = (1..values.length())
            .mapNotNull { values.get(it)?.toString() }
            .filter { it.isNotEmpty() }
    }

    @Synchronized
    fun clear(id: ResourceLocation) {
        lists.remove(id)
    }

    @Synchronized
    fun get(id: ResourceLocation): List<String> = lists[id].orEmpty()

    fun suggest(id: ResourceLocation, builder: SuggestionsBuilder): CompletableFuture<com.mojang.brigadier.suggestion.Suggestions> {
        val remaining = builder.remaining.lowercase(Locale.ROOT)
        get(id)
            .filter { it.lowercase(Locale.ROOT).startsWith(remaining) }
            .forEach { builder.suggest(it) }
        return builder.buildFuture()
    }
}

@LuaProvider(InitEngine.PROVIDER)
abstract class AbstractCommandHandler : ILuaStaticDecl by InitGenerated.AbstractCommandHandler_LuaGenerated {
    val subCommands = mutableListOf<AbstractCommandHandler>()

    /**
     * 여기에서 설정한 id를 register_endpoint를 통해 등록할 수 있습니다.
     * @param of endpoint id
     */
    @LuaFunction("set_endpoint")
    fun setEndpoint(of: String) {
        endpoint = RegistryHelper.getResourceLocation(of)
    }

    @LuaFunction
    fun append(of: AbstractCommandHandler) {
        subCommands.add(of)
    }

    /**
     * 이 인수의 자동완성 리스트를 설정합니다.
     * @param of 자동완성에 사용할 리스트 id
     */
    @LuaFunction("set_suggestion_list")
    fun setSuggestionList(of: String) {
        suggestionList = RegistryHelper.getResourceLocation(of)
    }

    var endpoint: ResourceLocation? = null
        set(value) {
            if (field != null)
                throw IllegalStateException("Cannot rewrite endpoint")
            field = value
        }

    private var parent: AbstractCommandHandler? = null
    private var suggestionList: ResourceLocation? = null

    protected abstract fun retrieve(): ArgumentBuilder<CommandSourceStack, *>
    protected abstract fun write(ctx: CommandContext<CommandSourceStack>, builder: CommandBuilder)

    fun build(): ArgumentBuilder<CommandSourceStack, *> {
        val b = retrieve()
        suggestionList?.let { suggestionList ->
            if (b is RequiredArgumentBuilder<CommandSourceStack, *>) {
                b.suggests { _, builder -> CommandSuggestionLists.suggest(suggestionList, builder) }
            }
        }
        subCommands.forEach {
            it.build(b)
            it.parent = this
        }
        endpoint?.let {
            b.executes {
                execute(it)
                1
            }
        }
        return b
    }

    fun <T : ArgumentBuilder<CommandSourceStack, T>> build(of: ArgumentBuilder<CommandSourceStack, T>) {
        of.then(build())
    }

    fun parse(ctx: CommandContext<CommandSourceStack>, builder: CommandBuilder) {
        write(ctx, builder)
        parent?.parse(ctx, builder)
    }

    fun execute(ctx: CommandContext<CommandSourceStack>) {
        val builder = CommandBuilder()
        parse(ctx, builder)
        CommandHooks.commandEndpointHook[endpoint
            ?: throw Exception("No endpoint declared to handle this command")].callAsTaskRawArg { task ->
            engine.luaMain.pushNoInline(task.coroutine, ctx.source.player?.let { LuaServerPlayer(it) })
            task.coroutine.newTable()
            for ((rl, act) in builder.inner)
                if (engine.luaMain.pushNoInline(task.coroutine, act) == 1)
                    task.coroutine.setField(-2, rl.path)
            2
        }
    }
}

class CommandBuilder {
    val inner = mutableMapOf<ResourceLocation, Any?>()
}
