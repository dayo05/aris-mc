package me.ddayo.aris.engine.client.wrapper

import me.ddayo.aris.engine.client.ClientMainEngine
import me.ddayo.aris.lua.glue.LuaClientOnlyGenerated
import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProperty
import me.ddayo.aris.luagen.LuaProvider
import net.minecraft.client.KeyMapping

@LuaProvider(ClientMainEngine.Companion.PROVIDER)
class LuaKeyBinding(val keybinding: KeyMapping) : ILuaStaticDecl by LuaClientOnlyGenerated.LuaKeyBinding_LuaGenerated {
    @LuaProperty("is_down")
    val isDown get() = keybinding.isDown
    @LuaFunction
    fun consume(): Boolean = keybinding.consumeClick()
}