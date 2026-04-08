package me.ddayo.aris.engine.wrapper

import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.lua.glue.InGameGenerated
import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.luagen.LuaProperty
import me.ddayo.aris.luagen.LuaProvider
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

@LuaProvider(InGameEngine.PROVIDER)
open class LuaPlayerEntity(val player: Player) : LuaLivingEntity(player), ILuaStaticDecl by InGameGenerated.LuaPlayerEntity_LuaGenerated {
    /**
     * 플레이어의 오른손의 아이템을 가져옵니다.
     */
    @LuaProperty("main_hand_item")
    open val mainHandItem
        get() = LuaItemStack(player.mainHandItem)

    /**
     * 플레이어가 웅크리고 있는지 여부를 가져옵니다.
     */
    @LuaProperty("is_sneaking")
    val isSneaking get() = player.isShiftKeyDown

    /**
     * 플레이어가 달리고 있는지 여부를 가져옵니다.
     */
    @LuaProperty("is_running")
    val isRunning get() = player.isSprinting

    override fun forEachItem(action: (ItemStack) -> Boolean) {
        for (i in 0 until player.inventory.containerSize) {
            if (action(player.inventory.getItem(i))) return
        }
    }

}