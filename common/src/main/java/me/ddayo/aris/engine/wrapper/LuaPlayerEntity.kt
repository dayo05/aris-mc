package me.ddayo.aris.engine.wrapper

import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.lua.glue.InGameGenerated
import me.ddayo.aris.luagen.LuaProperty
import me.ddayo.aris.luagen.LuaProvider
import net.minecraft.world.entity.player.Player

@LuaProvider(InGameEngine.PROVIDER)
open class LuaPlayerEntity(val player: Player) : LuaEntity(player), ILuaStaticDecl by InGameGenerated.LuaPlayerEntity_LuaGenerated {
    /**
     * 플레이어의 오른손의 아이템을 가져옵니다.
     */
    @LuaProperty("main_hand_item")
    open val mainHandItem
        get() = LuaItemStack(player.mainHandItem)
}