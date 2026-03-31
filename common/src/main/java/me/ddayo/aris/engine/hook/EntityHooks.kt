package me.ddayo.aris.engine.hook

import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.engine.wrapper.LuaDamageSource
import me.ddayo.aris.engine.wrapper.LuaEntity.Companion.toLuaValue
import me.ddayo.aris.engine.wrapper.LuaItemMoveEvent
import me.ddayo.aris.engine.wrapper.LuaItemStack
import me.ddayo.aris.engine.wrapper.LuaServerPlayer
import me.ddayo.aris.luagen.LuaFunc
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack

@LuaProvider(InGameEngine.PROVIDER, library = "aris.game.hook")
object EntityHooks {
    val itemUseHook = LuaHookMap<String>()
    init {
        InGameEngine.hookMaps.add(itemUseHook)
    }

    /**
     * 추가한 아이템을 사용했을때 실행할 함수를 추가합니다.
     * @param item 아이템 id
     * @param func 실행할 함수
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

    val rightClickHook = LuaHook()
    init {
        InGameEngine.hooks.add(rightClickHook)
    }

    /**
     * 플레이어가 임의의 위치를 우클릭시 실행할 함수
     * @param f 실행할 함수
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

    val onEntityDamagedHook = LuaHook()
    init {
        InGameEngine.hooks.add(onEntityDamagedHook)
    }

    /**
     * 플레이어가 데미지를 입었을 때 실행할 함수
     * @param f 실행할 함수
     */
    @LuaFunction("add_on_entity_damaged")
    fun onEntityGotDamaged(f: LuaFunc) {
        onEntityDamagedHook.add(f)
    }

    fun executeOnEntityGotDamage(damage: DamageSource, amount: Float, target: LivingEntity): Float {
        val incr = LuaDamageSource(damage, amount)
        onEntityDamagedHook.call(incr, target.toLuaValue())
        return incr.amount
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

    fun executeOnContainerClick(player: ServerPlayer, item: ItemStack): Boolean {
        val event = LuaItemMoveEvent(LuaServerPlayer(player), LuaItemStack(item), "container_click")
        onItemMoveHook.call(event)
        return event.cancelled
    }

    fun executeOnItemDrop(player: ServerPlayer, item: ItemStack): Boolean {
        val event = LuaItemMoveEvent(LuaServerPlayer(player), LuaItemStack(item), "drop")
        onItemMoveHook.call(event)
        return event.cancelled
    }

    fun executeOnItemPickup(player: ServerPlayer, item: ItemStack): Boolean {
        val event = LuaItemMoveEvent(LuaServerPlayer(player), LuaItemStack(item), "pickup")
        onItemMoveHook.call(event)
        return event.cancelled
    }
}