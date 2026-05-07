package me.ddayo.aris.engine.hook

import me.ddayo.aris.Aris
import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.engine.wrapper.*
import me.ddayo.aris.engine.wrapper.LuaEntity.Companion.toLuaValue
import me.ddayo.aris.luagen.LuaFunc
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import org.apache.logging.log4j.LogManager
import java.util.function.Supplier

@LuaProvider(InGameEngine.PROVIDER, library = "aris.game.hook")
object EntityHooks {
    private val logger = LogManager.getLogger()

    /**
     * Run [block] on the server thread. If already on it, runs immediately;
     * otherwise it is queued onto the server's event loop. LuaJIT is not
     * thread-safe, so every Lua state interaction must happen on a single
     * (server) thread.
     */
    private inline fun onServerThread(crossinline block: () -> Unit) {
        val server = Aris.server
        if (server.isSameThread) block() else server.execute { block() }
    }

    /**
     * For hooks that must return a value synchronously (cancel/modify), we
     * submit the work to the server thread and block until it returns. This
     * keeps the Lua state single-threaded and the return value correct.
     * Deadlock is possible (e.g. if the calling thread holds a lock the
     * server thread is waiting on) and is accepted as the cost of correctness.
     */
    private inline fun <T> requireServerThread(name: String, crossinline block: () -> T): T {
        val server = Aris.server
        if (server.isSameThread) return block()
        logger.warn(
            "$name invoked off the server thread (${Thread.currentThread().name}); " +
                    "blocking until the server thread runs the hook"
        )
        return server.submit(Supplier { block() }).get()
    }

    val itemUseHook = LuaHookMap<String>()
    init {
        InGameEngine.hookMaps.add(itemUseHook)
    }

    /**
     * 추가한 아이템을 사용했을때 실행할 함수를 추가합니다.
     * @param item 아이템 id
     * @param func 실행할 함수 (LuaUseItemEvent를 인자로 받음)
     */
    @LuaFunction("add_on_use_item")
    fun onUseItemHook(item: String, func: LuaFunc) {
        itemUseHook[item].add(func)
    }

    /**
     * add_on_use_item을 통해 등록한 함수들을 초기화합니다.
     * @param item 초기화할 아이템
     */
    @LuaFunction("clear_on_use_item")
    fun clearOnUseItem(item: String) {
        itemUseHook[item].clear()
    }

    fun executeOnUseItem(itemId: String, player: ServerPlayer, item: ItemStack) {
        onServerThread {
            val event = LuaUseItemEvent(LuaServerPlayer(player), LuaItemStack(item))
            itemUseHook[itemId].callAsTask(event)
        }
    }

    val rightClickHook = LuaHook()
    init {
        InGameEngine.hooks.add(rightClickHook)
    }

    /**
     * 플레이어가 임의의 위치를 우클릭시 실행할 함수
     * @param f 실행할 함수 (LuaRightClickEvent를 인자로 받음)
     */
    @LuaFunction("add_on_right_click")
    fun onRightClick(f: LuaFunc) {
        rightClickHook.add(f)
    }

    /**
     * 플레이어가 임의의 위치를 우클릭시 실행할 훅 초기화
     */
    @LuaFunction("clear_on_right_click")
    fun clearOnRightClick() {
        rightClickHook.clear()
    }

    fun executeOnRightClick(player: ServerPlayer) {
        onServerThread {
            val event = LuaRightClickEvent(LuaServerPlayer(player))
            rightClickHook.callAsTask(event)
        }
    }

    val leftClickHook = LuaHook()
    init {
        InGameEngine.hooks.add(leftClickHook)
    }

    /**
     * 플레이어가 임의의 위치를 좌클릭시 실행할 함수
     * @param f 실행할 함수 (LuaLeftClickEvent를 인자로 받음)
     */
    @LuaFunction("add_on_left_click")
    fun onLeftClick(f: LuaFunc) {
        leftClickHook.add(f)
    }

    /**
     * 플레이어가 임의의 위치를 좌클릭시 실행할 훅 초기화
     */
    @LuaFunction("clear_on_left_click")
    fun clearOnLeftClick() {
        leftClickHook.clear()
    }

    fun executeOnLeftClick(player: ServerPlayer) {
        onServerThread {
            val event = LuaLeftClickEvent(LuaServerPlayer(player))
            leftClickHook.callAsTask(event)
        }
    }

    val onEntityDamagedHook = LuaHook()
    init {
        InGameEngine.hooks.add(onEntityDamagedHook)
    }

    /**
     * 엔티티가 데미지를 입었을 때 실행할 함수
     * @param f 실행할 함수 (LuaEntityDamagedEvent를 인자로 받음)
     */
    @LuaFunction("add_on_entity_damaged")
    fun onEntityGotDamaged(f: LuaFunc) {
        onEntityDamagedHook.add(f)
    }

    fun executeOnEntityGotDamage(damage: DamageSource, amount: Float, target: LivingEntity): Float =
        requireServerThread("executeOnEntityGotDamage") {
            val event = LuaEntityDamagedEvent(LuaDamageSource(damage, amount), target.toLuaValue())
            onEntityDamagedHook.call(event)
            event.damageSourceWrapper.amount
        }

    val onItemMoveHook = LuaHook()
    init {
        InGameEngine.hooks.add(onItemMoveHook)
    }

    /**
     * 아이템 이동 시 실행할 함수를 추가합니다.
     * 컨테이너 클릭, 아이템 드롭, 아이템 줍기 등을 감지합니다.
     * event:cancel()을 호출하면 이동을 취소합니다.
     * @param f 실행할 함수 (LuaItemMoveEvent를 인자로 받음)
     */
    @LuaFunction("add_on_item_move")
    fun onItemMove(f: LuaFunc) {
        onItemMoveHook.add(f)
    }

    /**
     * 아이템 이동 훅을 초기화합니다.
     */
    @LuaFunction("clear_on_item_move")
    fun clearOnItemMove() {
        onItemMoveHook.clear()
    }

    fun executeOnContainerClick(player: ServerPlayer, item: ItemStack): Boolean =
        requireServerThread("executeOnContainerClick") {
            val event = LuaItemMoveEvent(LuaServerPlayer(player), LuaItemStack(item), "container_click")
            onItemMoveHook.call(event)
            event.cancelled
        }

    fun executeOnItemDrop(player: ServerPlayer, item: ItemStack): Boolean =
        requireServerThread("executeOnItemDrop") {
            val event = LuaItemMoveEvent(LuaServerPlayer(player), LuaItemStack(item), "drop")
            onItemMoveHook.call(event)
            event.cancelled
        }

    fun executeOnItemPickup(player: ServerPlayer, item: ItemStack): Boolean =
        requireServerThread("executeOnItemPickup") {
            val event = LuaItemMoveEvent(LuaServerPlayer(player), LuaItemStack(item), "pickup")
            onItemMoveHook.call(event)
            event.cancelled
        }
}