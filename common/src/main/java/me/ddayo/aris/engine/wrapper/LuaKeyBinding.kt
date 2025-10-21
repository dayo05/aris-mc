package me.ddayo.aris.engine.wrapper

import me.ddayo.aris.engine.client.ClientMainEngine
import me.ddayo.aris.lua.glue.LuaClientOnlyGenerated
import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.luagen.LuaProperty
import me.ddayo.aris.luagen.LuaProvider
import net.minecraft.client.KeyMapping

@LuaProvider(ClientMainEngine.PROVIDER)
class LuaKeyBinding(val keybinding: KeyMapping) : ILuaStaticDecl by LuaClientOnlyGenerated.LuaKeyBinding_LuaGenerated {
    @LuaProperty("is_down")
    val isDown get() = keybinding.isDown
    @LuaProperty
    val consume get() = keybinding.consumeClick()
}