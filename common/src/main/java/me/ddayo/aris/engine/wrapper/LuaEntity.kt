package me.ddayo.aris.engine.wrapper

import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.luagen.LuaFunc
import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.engine.wrapper.LuaServerPlayerFunctions.coroutine
import me.ddayo.aris.lua.glue.InGameGenerated
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProperty
import me.ddayo.aris.luagen.LuaProvider
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.npc.InventoryCarrier
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.AABB
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


@LuaProvider(InGameEngine.PROVIDER)
open class LuaEntity(val inner: Entity) : ILuaStaticDecl by InGameGenerated.LuaEntity_LuaGenerated {
    companion object {
        fun Entity.toLuaValue() = when (this) {
            is ServerPlayer -> LuaServerPlayer(this)
            is Player -> LuaPlayerEntity(this)
            is LivingEntity -> LuaLivingEntity(this)
            else -> LuaEntity(this)
        }
    }

    /**
     * 엔티티의 이름을 가져옵니다.
     */
    @LuaProperty("name")
    val name get() = inner.name.string

    /**
     * 엔티티의 타입 ID를 가져옵니다. (예: "minecraft:zombie")
     */
    @LuaProperty
    val type get() = BuiltInRegistries.ENTITY_TYPE.getKey(inner.type).toString()

    /**
     * 엔티티의 표시된 이름을 가져옵니다.
     */
    @LuaProperty("display_name")
    val displayName get() = inner.displayName?.string ?: name

    /**
     * 엔티티의 커스텀 이름을 설정하거나 가져올 수 있습니다.
     */
    @LuaProperty("custom_name")
    var customName
        get() = inner.customName?.string ?: "None"
        set(value) {
            inner.customName = Component.literal(value)
        }

    /**
     * 엔티티의 타입 객체를 가져옵니다.
     */
    @LuaProperty("entity_type")
    val entityType get() = LuaEntityType(inner.type)

    /**
     * 플레이어의 X좌표를 가져오거나 설정할 수 있습니다.
     */
    @LuaProperty
    var x
        get() = inner.x
        set(value) {
            inner.teleportTo(value, inner.y, inner.z)
        }

    /**
     * 엔티티의 Y좌표를 가져오거나 설정할 수 있습니다.
     */
    @LuaProperty
    var y
        get() = inner.y
        set(value) {
            inner.teleportTo(inner.x, value, inner.z)
        }

    /**
     * 엔티티의 Z좌표를 가져오거나 설정할 수 있습니다.
     */
    @LuaProperty
    var z
        get() = inner.z
        set(value) {
            inner.teleportTo(inner.x, inner.y, value)
        }

    /**
     * 엔티티의 uuid를 가져옵니다.
     */
    @LuaProperty
    val uuid get() = inner.stringUUID

    /**
     * 엔티티의 world를 가져옵니다.
     */
    @LuaProperty(name = "server_world")
    val serverWorld get() = LuaServerWorld(inner.level() as ServerLevel)

    /**
     * 엔티티에 특정 데미지를 줄 수 있습니다.
     */
    @LuaFunction(name = "add_damage")
    fun addDamage(damage: Double) {
        inner.hurt(inner.damageSources().generic(), damage.toFloat())
    }

    /**
     * 엔티티에 속도를 설정합니다.
     */
    @LuaFunction("add_velocity")
    fun addVelocity(x: Double, y: Double, z: Double) {
        inner.let {
            it.setDeltaMovement(x, y, z)
            it.hurtMarked = true
            it.hasImpulse = true
        }
    }

    /**
     * 엔티티가 바라보는 방향을 기준으로 속도를 설정합니다.
     */
    @LuaFunction("add_velocity_relative")
    fun addVelocityRelative(x: Double, y: Double, z: Double) {
        val yawRad = Math.toRadians(inner.yRot.toDouble())

        // Forward vector (XZ plane only, ignoring pitch)
        val forwardX = -sin(yawRad)
        val forwardZ = cos(yawRad)

        // Right vector (perpendicular to forward vector)
        val rightX = cos(yawRad)
        val rightZ = sin(yawRad)

        // Calculate the new position
        val newX = forwardX * x + rightX * z
        val newY = y
        val newZ = forwardZ * x + rightZ * z
        addVelocity(newX, newY, newZ)
    }

    /**
     * 엔티티를 특정 상대적인 위치로 텔레포트 시킵니다.
     * @param x 이동시킬 x좌표의 상대적인 값
     * @param y 이동시킬 y좌표의 상대적인 값
     * @param z 이동시킬 z좌표의 상대적인 값
     */
    @LuaFunction("move_delta")
    fun moveDelta(x: Double, y: Double, z: Double) {
        inner.teleportTo(inner.x + x, inner.y + y, inner.z + z)
    }

    /**
     * 엔티티를 특정 위치로 텔레포트 시킵니다.
     * @param x 이동시킬 x좌표
     * @param y 이동시킬 y좌표
     * @param z 이동시킬 z좌표
     */
    @LuaFunction("move_to")
    fun moveTo(x: Double, y: Double, z: Double) {
        inner.teleportTo(x, y, z)
    }

    /**
     * 엔티티를 바라보는 위치를 기준으로 하는 상대적인 위치로 텔레포트 시킵니다.
     * @param x 앞으로 이동할 칸수
     * @param y 위로 이동할 칸수
     * @param z 옆으로 이동할 칸수(+는 오른쪽을 의미)
     */
    @LuaFunction("move_delta_relative")
    fun moveDeltaRelative(x: Double, y: Double, z: Double) {
        val yawRad = Math.toRadians(inner.yRot.toDouble())

        // Forward vector (XZ plane only, ignoring pitch)
        val forwardX = -sin(yawRad)
        val forwardZ = cos(yawRad)

        // Right vector (perpendicular to forward vector)
        val rightX = cos(yawRad)
        val rightZ = sin(yawRad)

        // Calculate the new position
        val newX = inner.x + forwardX * x + rightX * z
        val newY = inner.y + y
        val newZ = inner.z + forwardZ * x + rightZ * z
        moveTo(newX, newY, newZ)
    }

    /**
     * 주변 엔티티를 순회합니다.
     * @param fn 각 엔티티에 대해 실행할 콜백
     * @param radius 탐색 반경
     * @param includeSelf 자기 자신을 포함할지 여부
     */
    @LuaFunction("iter_entities_nearby")
    fun getEntitiesNearby(fn: LuaFunc, radius: Double, includeSelf: Boolean) = coroutine<Unit> {
        val level = inner.level()
        val area: AABB? = inner.boundingBox.inflate(radius)
        level.getEntities(inner, area) {
            if (includeSelf || it != inner)
                fn.await(this, LuaEntity(it))
            true
        }
    }

    /**
     * 엔티티를 월드에서 즉시 제거합니다. (사망 애니메이션 없음, 드롭 없음)
     */
    @LuaFunction("remove")
    fun remove() {
        inner.discard()
    }
}

@LuaProvider(InGameEngine.PROVIDER)
open class LuaLivingEntity(val living: LivingEntity) : LuaEntity(living),
    ILuaStaticDecl by InGameGenerated.LuaLivingEntity_LuaGenerated {
    /**
     * 엔티티에 상태 효과를 추가합니다.
     */
    @LuaFunction(name = "add_effect")
    fun addEffect(effect: LuaMobEffectInstance) {
        living.addEffect(effect.build())
    }

    /**
     * 엔티티의 모든 상태 효과를 제거합니다.
     */
    @LuaFunction(name = "clear_effect")
    fun clearEffect() {
        living.removeAllEffects()
    }

    /**
     * 엔티티의 특정 상태 효과를 제거합니다.
     * @param of 효과 ID (예: "minecraft:speed")
     */
    @LuaFunction(name = "remove_effect")
    fun removeEffect(of: String) {
        living.removeEffect(BuiltInRegistries.MOB_EFFECT.get(ResourceLocation.parse(of)).get())
    }

    /**
     * 엔티티의 특정 상태 효과를 제거합니다.
     * @param ns 네임스페이스
     * @param of 효과 이름
     */
    @LuaFunction(name = "remove_effect")
    fun removeEffect(ns: String, of: String) {
        living.removeEffect(BuiltInRegistries.MOB_EFFECT.get(ResourceLocation.tryBuild(ns, of)!!).get())
    }

    /**
     * 엔티티의 pitch(상하 회전)를 가져오거나 설정합니다.
     */
    @LuaProperty
    var pitch
        get() = living.xRot
        set(value) {
            living.xRot = value
        }

    /**
     * 엔티티의 yaw(좌우 회전)를 가져오거나 설정합니다.
     */
    @LuaProperty
    var yaw
        get() = living.yRot
        set(value) {
            living.yRot = value
        }

    /**
     * 엔티티의 모든 아이템을 순회합니다. (장비 슬롯 + 인벤토리)
     * action이 true를 반환하면 순회를 중단합니다.
     */
    protected open fun forEachItem(action: (ItemStack) -> Boolean) {
        // 장비 슬롯
        for (slot in EquipmentSlot.entries) {
            if (action(living.getItemBySlot(slot))) return
        }
        // InventoryCarrier 인터페이스 (마을 주민, 피글린, 알레이 등)
        if (living is InventoryCarrier) {
            val inv = (living as InventoryCarrier).inventory
            for (i in 0 until inv.containerSize) {
                if (action(inv.getItem(i))) return
            }
        }
    }

    companion object {
        private fun parseEquipmentSlot(slot: String): EquipmentSlot? = when (slot.lowercase()) {
            "mainhand", "main_hand" -> EquipmentSlot.MAINHAND
            "offhand", "off_hand" -> EquipmentSlot.OFFHAND
            "head", "helmet" -> EquipmentSlot.HEAD
            "chest", "chestplate" -> EquipmentSlot.CHEST
            "legs", "leggings" -> EquipmentSlot.LEGS
            "feet", "boots" -> EquipmentSlot.FEET
            else -> null
        }
    }

    /**
     * 장비 슬롯의 아이템을 가져옵니다.
     * 슬롯: mainhand, offhand, head, chest, legs, feet
     */
    @LuaFunction(name = "get_equipment")
    fun getEquipment(slot: String): LuaItemStack? {
        val equipSlot = parseEquipmentSlot(slot) ?: return null
        return LuaItemStack(living.getItemBySlot(equipSlot))
    }

    /**
     * 장비 슬롯에 아이템을 설정합니다.
     * 슬롯: mainhand, offhand, head, chest, legs, feet
     */
    @LuaFunction(name = "set_equipment")
    fun setEquipment(slot: String, item: LuaItemStack) {
        val equipSlot = parseEquipmentSlot(slot) ?: return
        living.setItemSlot(equipSlot, item.inner)
    }

    /**
     * 장비 슬롯의 아이템을 제거합니다.
     * 슬롯: mainhand, offhand, head, chest, legs, feet
     */
    @LuaFunction(name = "clear_equipment")
    fun clearEquipment(slot: String) {
        val equipSlot = parseEquipmentSlot(slot) ?: return
        living.setItemSlot(equipSlot, ItemStack.EMPTY)
    }

    /**
     * 슬롯 번호로 아이템을 가져옵니다.
     * @param slot 슬롯 번호
     */
    @LuaFunction("get_slot")
    open fun getSlot(slot: Int): LuaItemStack {
        return LuaItemStack(living.getSlot(slot).get())
    }

    /**
     * 슬롯 번호로 아이템을 설정합니다.
     * @param slot 슬롯 번호
     * @param item 설정할 아이템
     */
    @LuaFunction("set_slot")
    open fun setSlot(slot: Int, item: LuaItemStack) {
        living.getSlot(slot).set(item.inner)
    }

    /**
     * 엔티티에 아이템을 추가합니다. 이미 같은 아이템이 있으면 수량을 합칩니다.
     * @param item 추가할 아이템
     * @return 성공 여부
     */
    @LuaFunction("give_item")
    open fun giveItem(item: LuaItemStack): Boolean {
        val stack = item.inner.copy()
        // 기존 슬롯에서 같은 아이템이 있으면 합치기
        forEachItem { existing ->
            if (!existing.isEmpty && ItemStack.isSameItemSameComponents(existing, stack)) {
                val canAdd = min(stack.count, existing.maxStackSize - existing.count)
                if (canAdd > 0) {
                    existing.grow(canAdd)
                    stack.shrink(canAdd)
                }
            }
            stack.isEmpty
        }
        if (stack.isEmpty) return true
        // 장비 슬롯에 빈 곳 찾기
        for (slot in EquipmentSlot.entries) {
            if (living.getItemBySlot(slot).isEmpty) {
                living.setItemSlot(slot, stack)
                return true
            }
        }
        // InventoryCarrier 인벤토리에 빈 곳 찾기
        if (living is InventoryCarrier) {
            val inv = (living as InventoryCarrier).inventory
            for (i in 0 until inv.containerSize) {
                if (inv.getItem(i).isEmpty) {
                    inv.setItem(i, stack)
                    return true
                }
            }
        }
        return false
    }

    /**
     * 아이템 ID와 수량으로 엔티티에 아이템을 추가합니다.
     * @param id 아이템 ID (예: "minecraft:diamond")
     * @param count 수량
     * @return 성공 여부
     */
    @LuaFunction("give_item")
    open fun giveItem(id: String, count: Int): Boolean {
        val holder = BuiltInRegistries.ITEM.get(ResourceLocation.parse(id)).get()
        return giveItem(LuaItemStack(ItemStack(holder, count)))
    }

    /**
     * 엔티티의 모든 아이템을 제거합니다.
     */
    @LuaFunction("clear_inventory")
    open fun clearInventory() {
        forEachItem { stack ->
            stack.count = 0
            false
        }
    }

    /**
     * 엔티티의 슬롯에서 특정 아이템을 제거합니다. 수량이 부족하면 제거하지 않고 false를 반환합니다.
     * @param id 아이템 ID (예: "minecraft:diamond")
     * @param count 제거할 수량
     * @return 성공 여부
     */
    @LuaFunction("remove_item")
    open fun removeItem(id: String, count: Int): Boolean {
        val targetItem = BuiltInRegistries.ITEM.get(ResourceLocation.parse(id)).get().value()
        // 먼저 충분한 수량이 있는지 확인
        var available = 0
        forEachItem { stack ->
            if (stack.item == targetItem) available += stack.count
            false
        }
        if (available < count) return false
        // 충분하므로 제거
        var remaining = count
        forEachItem { stack ->
            if (remaining > 0 && stack.item == targetItem) {
                val toRemove = min(remaining, stack.count)
                stack.shrink(toRemove)
                remaining -= toRemove
            }
            remaining <= 0
        }
        return true
    }
}
