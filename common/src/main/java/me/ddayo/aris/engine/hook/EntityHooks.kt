package me.ddayo.aris.engine.hook

import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.engine.wrapper.*
import me.ddayo.aris.engine.wrapper.LuaEntity.Companion.toLuaValue
import me.ddayo.aris.luagen.LuaCallback
import me.ddayo.aris.luagen.LuaCallbackParam
import me.ddayo.aris.luagen.LuaFunc
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import net.minecraft.server.level.ServerPlayer
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

@LuaProvider(InGameEngine.PROVIDER, library = "aris.game.hook")
object EntityHooks {
    data class EntityDamageResult(val cancelled: Boolean, val amount: Float)

    private fun blockId(state: BlockState) = BuiltInRegistries.BLOCK.getKey(state.block).toString()

    val blockLeftClickHook = LuaHook()
    init {
        InGameEngine.hooks.add(blockLeftClickHook)
    }

    /**
     * 플레이어가 블록을 좌클릭했을 때 실행할 함수를 추가합니다.
     * event:cancel()을 호출하면 블록 파괴 시작을 취소합니다.
     * @param f 실행할 함수 (LuaBlockEvent를 인자로 받음)
     */
    @LuaFunction("add_on_block_left_click")
    fun onBlockLeftClick(
        @LuaCallback(params = [LuaCallbackParam("event", LuaBlockEvent::class)])
        f: LuaFunc
    ) {
        blockLeftClickHook.add(f)
    }

    /**
     * 블록 좌클릭 훅을 초기화합니다.
     */
    @LuaFunction("clear_on_block_left_click")
    fun clearOnBlockLeftClick() {
        blockLeftClickHook.clear()
    }

    fun executeOnBlockLeftClick(player: ServerPlayer, pos: BlockPos, direction: Direction): Boolean {
        val event = LuaBlockEvent(
            LuaServerPlayer(player),
            pos.x,
            pos.y,
            pos.z,
            blockId(player.level().getBlockState(pos)),
            direction.serializedName,
            "left_click",
            "main_hand"
        )
        blockLeftClickHook.call(event)
        return event.cancelled
    }

    val blockRightClickHook = LuaHook()
    init {
        InGameEngine.hooks.add(blockRightClickHook)
    }

    /**
     * 플레이어가 블록을 우클릭했을 때 실행할 함수를 추가합니다.
     * event:cancel()을 호출하면 블록 상호작용을 취소합니다.
     * @param f 실행할 함수 (LuaBlockEvent를 인자로 받음)
     */
    @LuaFunction("add_on_block_right_click")
    fun onBlockRightClick(
        @LuaCallback(params = [LuaCallbackParam("event", LuaBlockEvent::class)])
        f: LuaFunc
    ) {
        blockRightClickHook.add(f)
    }

    /**
     * 블록 우클릭 훅을 초기화합니다.
     */
    @LuaFunction("clear_on_block_right_click")
    fun clearOnBlockRightClick() {
        blockRightClickHook.clear()
    }

    fun executeOnBlockRightClick(player: ServerPlayer, pos: BlockPos, direction: Direction, hand: InteractionHand): Boolean {
        val event = LuaBlockEvent(
            LuaServerPlayer(player),
            pos.x,
            pos.y,
            pos.z,
            blockId(player.level().getBlockState(pos)),
            direction.serializedName,
            "right_click",
            hand.name.lowercase()
        )
        blockRightClickHook.call(event)
        return event.cancelled
    }

    val blockBreakHook = LuaHook()
    init {
        InGameEngine.hooks.add(blockBreakHook)
    }

    /**
     * 플레이어가 블록을 파괴하기 직전에 실행할 함수를 추가합니다.
     * event:cancel()을 호출하면 블록 파괴를 취소합니다.
     * @param f 실행할 함수 (LuaBlockEvent를 인자로 받음)
     */
    @LuaFunction("add_on_block_break")
    fun onBlockBreak(
        @LuaCallback(params = [LuaCallbackParam("event", LuaBlockEvent::class)])
        f: LuaFunc
    ) {
        blockBreakHook.add(f)
    }

    /**
     * 블록 파괴 훅을 초기화합니다.
     */
    @LuaFunction("clear_on_block_break")
    fun clearOnBlockBreak() {
        blockBreakHook.clear()
    }

    fun executeOnBlockBreak(player: ServerPlayer, pos: BlockPos): Boolean {
        val event = LuaBlockEvent(
            LuaServerPlayer(player),
            pos.x,
            pos.y,
            pos.z,
            blockId(player.level().getBlockState(pos)),
            "unknown",
            "break",
            "main_hand"
        )
        blockBreakHook.call(event)
        return event.cancelled
    }

    val blockPlaceHook = LuaHook()
    init {
        InGameEngine.hooks.add(blockPlaceHook)
    }

    /**
     * 플레이어가 블록을 설치하려고 할 때 실행할 함수를 추가합니다.
     * event:cancel()을 호출하면 블록 설치를 취소합니다.
     * @param f 실행할 함수 (LuaBlockEvent를 인자로 받음)
     */
    @LuaFunction("add_on_block_place")
    fun onBlockPlace(
        @LuaCallback(params = [LuaCallbackParam("event", LuaBlockEvent::class)])
        f: LuaFunc
    ) {
        blockPlaceHook.add(f)
    }

    /**
     * 블록 설치 훅을 초기화합니다.
     */
    @LuaFunction("clear_on_block_place")
    fun clearOnBlockPlace() {
        blockPlaceHook.clear()
    }

    fun executeOnBlockPlace(player: ServerPlayer, pos: BlockPos, direction: Direction, hand: InteractionHand, block: Block): Boolean {
        val event = LuaBlockEvent(
            LuaServerPlayer(player),
            pos.x,
            pos.y,
            pos.z,
            BuiltInRegistries.BLOCK.getKey(block).toString(),
            direction.serializedName,
            "place",
            hand.name.lowercase()
        )
        blockPlaceHook.call(event)
        return event.cancelled
    }

    val entityInteractHook = LuaHook()
    init {
        InGameEngine.hooks.add(entityInteractHook)
    }

    /**
     * 플레이어가 엔티티를 우클릭했을 때 실행할 함수를 추가합니다.
     * event:cancel()을 호출하면 엔티티 상호작용을 취소합니다.
     * @param f 실행할 함수 (LuaEntityInteractEvent를 인자로 받음)
     */
    @LuaFunction("add_on_entity_interact")
    fun onEntityInteract(
        @LuaCallback(params = [LuaCallbackParam("event", LuaEntityInteractEvent::class)])
        f: LuaFunc
    ) {
        entityInteractHook.add(f)
    }

    /**
     * 엔티티 우클릭 훅을 초기화합니다.
     */
    @LuaFunction("clear_on_entity_interact")
    fun clearOnEntityInteract() {
        entityInteractHook.clear()
    }

    fun executeOnEntityInteract(player: ServerPlayer, target: Entity, hand: InteractionHand): Boolean {
        val event = LuaEntityInteractEvent(LuaServerPlayer(player), target.toLuaValue(), "interact", hand.name.lowercase())
        entityInteractHook.call(event)
        return event.cancelled
    }

    val entityAttackHook = LuaHook()
    init {
        InGameEngine.hooks.add(entityAttackHook)
    }

    /**
     * 플레이어가 엔티티를 공격하려고 할 때 실행할 함수를 추가합니다.
     * event:cancel()을 호출하면 엔티티 공격을 취소합니다.
     * @param f 실행할 함수 (LuaEntityInteractEvent를 인자로 받음)
     */
    @LuaFunction("add_on_entity_attack")
    fun onEntityAttack(
        @LuaCallback(params = [LuaCallbackParam("event", LuaEntityInteractEvent::class)])
        f: LuaFunc
    ) {
        entityAttackHook.add(f)
    }

    /**
     * 엔티티 공격 훅을 초기화합니다.
     */
    @LuaFunction("clear_on_entity_attack")
    fun clearOnEntityAttack() {
        entityAttackHook.clear()
    }

    fun executeOnEntityAttack(player: ServerPlayer, target: Entity): Boolean {
        val event = LuaEntityInteractEvent(LuaServerPlayer(player), target.toLuaValue(), "attack", "main_hand")
        entityAttackHook.call(event)
        return event.cancelled
    }

    val playerDeathHook = LuaHook()
    init {
        InGameEngine.hooks.add(playerDeathHook)
    }

    /**
     * 플레이어가 사망했을 때 실행할 함수를 추가합니다.
     * @param f 실행할 함수 (LuaPlayerEvent를 인자로 받음)
     */
    @LuaFunction("add_on_player_death")
    fun onPlayerDeath(
        @LuaCallback(params = [LuaCallbackParam("event", LuaPlayerEvent::class)])
        f: LuaFunc
    ) {
        playerDeathHook.add(f)
    }

    /**
     * 플레이어 사망 훅을 초기화합니다.
     */
    @LuaFunction("clear_on_player_death")
    fun clearOnPlayerDeath() {
        playerDeathHook.clear()
    }

    fun executeOnPlayerDeath(player: ServerPlayer) {
        playerDeathHook.callAsTask(LuaPlayerEvent(LuaServerPlayer(player), "death"))
    }

    val playerRespawnHook = LuaHook()
    init {
        InGameEngine.hooks.add(playerRespawnHook)
    }

    /**
     * 플레이어가 리스폰했을 때 실행할 함수를 추가합니다.
     * @param f 실행할 함수 (LuaPlayerEvent를 인자로 받음)
     */
    @LuaFunction("add_on_player_respawn")
    fun onPlayerRespawn(
        @LuaCallback(params = [LuaCallbackParam("event", LuaPlayerEvent::class)])
        f: LuaFunc
    ) {
        playerRespawnHook.add(f)
    }

    /**
     * 플레이어 리스폰 훅을 초기화합니다.
     */
    @LuaFunction("clear_on_player_respawn")
    fun clearOnPlayerRespawn() {
        playerRespawnHook.clear()
    }

    fun executeOnPlayerRespawn(player: ServerPlayer) {
        playerRespawnHook.callAsTask(LuaPlayerEvent(LuaServerPlayer(player), "respawn"))
    }

    val sneakHook = LuaHook()
    init {
        InGameEngine.hooks.add(sneakHook)
    }

    /**
     * 플레이어가 웅크리기를 시작하거나 해제했을 때 실행할 함수를 추가합니다.
     * event:get_is_release()가 true이면 웅크리기 해제, false이면 웅크리기 시작입니다.
     * @param f 실행할 함수 (LuaSneakEvent를 인자로 받음)
     */
    @LuaFunction("add_on_sneak")
    fun onSneak(
        @LuaCallback(params = [LuaCallbackParam("event", LuaSneakEvent::class)])
        f: LuaFunc
    ) {
        sneakHook.add(f)
    }

    /**
     * 웅크리기 훅을 초기화합니다.
     */
    @LuaFunction("clear_on_sneak")
    fun clearOnSneak() {
        sneakHook.clear()
    }

    fun executeOnSneak(player: ServerPlayer, isRelease: Boolean) {
        sneakHook.callAsTask(LuaSneakEvent(LuaServerPlayer(player), isRelease))
    }

    val itemConsumeHook = LuaHook()
    init {
        InGameEngine.hooks.add(itemConsumeHook)
    }

    /**
     * 플레이어가 아이템 소비를 완료했을 때 실행할 함수를 추가합니다.
     * @param f 실행할 함수 (LuaItemConsumeEvent를 인자로 받음)
     */
    @LuaFunction("add_on_item_consume")
    fun onItemConsume(
        @LuaCallback(params = [LuaCallbackParam("event", LuaItemConsumeEvent::class)])
        f: LuaFunc
    ) {
        itemConsumeHook.add(f)
    }

    /**
     * 아이템 소비 훅을 초기화합니다.
     */
    @LuaFunction("clear_on_item_consume")
    fun clearOnItemConsume() {
        itemConsumeHook.clear()
    }

    fun executeOnItemConsume(player: ServerPlayer, item: ItemStack) {
        if (item.isEmpty) return
        itemConsumeHook.callAsTask(LuaItemConsumeEvent(LuaServerPlayer(player), LuaItemStack(item.copy())))
    }

    val chatHook = LuaHook()
    init {
        InGameEngine.hooks.add(chatHook)
    }

    /**
     * 플레이어가 채팅을 보냈을 때 실행할 함수를 추가합니다.
     * event:cancel()을 호출하면 채팅 전송을 취소합니다.
     * @param f 실행할 함수 (LuaChatEvent를 인자로 받음)
     */
    @LuaFunction("add_on_chat")
    fun onChat(
        @LuaCallback(params = [LuaCallbackParam("event", LuaChatEvent::class)])
        f: LuaFunc
    ) {
        chatHook.add(f)
    }

    /**
     * 채팅 훅을 초기화합니다.
     */
    @LuaFunction("clear_on_chat")
    fun clearOnChat() {
        chatHook.clear()
    }

    fun executeOnChat(player: ServerPlayer, message: String): Boolean {
        val event = LuaChatEvent(LuaServerPlayer(player), message)
        chatHook.call(event)
        return event.cancelled
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
    fun onUseItemHook(
        item: String,
        @LuaCallback(params = [LuaCallbackParam("event", LuaUseItemEvent::class)])
        func: LuaFunc
    ) {
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
        val event = LuaUseItemEvent(LuaServerPlayer(player), LuaItemStack(item))
        itemUseHook[itemId].callAsTask(event)
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
    fun onRightClick(
        @LuaCallback(params = [LuaCallbackParam("event", LuaRightClickEvent::class)])
        f: LuaFunc
    ) {
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
        val event = LuaRightClickEvent(LuaServerPlayer(player))
        rightClickHook.callAsTask(event)
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
    fun onLeftClick(
        @LuaCallback(params = [LuaCallbackParam("event", LuaLeftClickEvent::class)])
        f: LuaFunc
    ) {
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
        val event = LuaLeftClickEvent(LuaServerPlayer(player))
        leftClickHook.callAsTask(event)
    }

    val onEntityDamagedHook = LuaHook()
    init {
        InGameEngine.hooks.add(onEntityDamagedHook)
    }

    /**
     * 엔티티가 데미지를 입었을 때 실행할 함수
     * event:cancel()을 호출하면 데미지를 취소합니다.
     * @param f 실행할 함수 (LuaEntityDamagedEvent를 인자로 받음)
     */
    @LuaFunction("add_on_entity_damaged")
    fun onEntityGotDamaged(
        @LuaCallback(params = [LuaCallbackParam("event", LuaEntityDamagedEvent::class)])
        f: LuaFunc
    ) {
        onEntityDamagedHook.add(f)
    }

    fun executeOnEntityGotDamage(damage: DamageSource, amount: Float, target: LivingEntity): EntityDamageResult {
        val event = LuaEntityDamagedEvent(LuaDamageSource(damage, amount), target.toLuaValue())
        onEntityDamagedHook.call(event)
        return EntityDamageResult(event.cancelled, event.damageSourceWrapper.amount)
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
    fun onItemMove(
        @LuaCallback(params = [LuaCallbackParam("event", LuaItemMoveEvent::class)])
        f: LuaFunc
    ) {
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
