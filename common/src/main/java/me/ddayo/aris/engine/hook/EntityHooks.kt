package me.ddayo.aris.engine.hook

import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.engine.wrapper.LuaDamageSource
import me.ddayo.aris.engine.wrapper.LuaEntity.Companion.toLuaValue
import me.ddayo.aris.luagen.LuaFunc
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity

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
}