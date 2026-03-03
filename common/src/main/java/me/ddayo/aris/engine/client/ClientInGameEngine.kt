package me.ddayo.aris.engine.client

import com.mojang.blaze3d.systems.RenderSystem
import me.ddayo.aris.client.KeyBindingHelper
import me.ddayo.aris.client.gui.HudRenderer
import me.ddayo.aris.client.gui.RenderUtil
import me.ddayo.aris.engine.AbstractPersistantEngineCompanion
import me.ddayo.aris.engine.hook.LuaHook
import me.ddayo.aris.engine.hook.LuaHookMap
import me.ddayo.aris.lua.glue.ClientInGameOnlyGenerated
import me.ddayo.aris.luagen.LuaFunc
import me.ddayo.aris.util.ListExtensions.mutableForEach
import net.minecraft.client.KeyMapping
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.item.ItemStack
import party.iroiro.luajava.Lua
import party.iroiro.luajava.luajit.LuaJit
import java.io.File

class ClientInGameEngine private constructor(lua: Lua) : ClientMainEngine(lua) {
    companion object: AbstractPersistantEngineCompanion<ClientInGameEngine>() {
        const val PROVIDER = "ClientInGameOnlyGenerated"

        override val searchPath = "robots/client-game"

        override fun _createEngine(lua: Lua) = ClientInGameEngine(lua)

        val tickHook = LuaHook()
        init {
            hooks.add(tickHook)
        }
    }

    override val basePath = File(searchPath)

    init {
        ClientInGameOnlyGenerated.initEngine(this)
        ClientEngineAddOn.clientInGameEngineAddOns().forEach {
            it.initLua(this)
        }
    }

    private val keyBindingHooks = mutableMapOf<KeyMapping, MutableList<LuaFunc>>()

    fun registerKeyHook(name: String, func: LuaFunc) {
        keyBindingHooks.getOrPut(KeyBindingHelper.getKey(name)) { mutableListOf() }
            .add(func)
    }

    fun tick() {
        tickHook.call()
        keyBindingHooks.forEach { (binding, keys) ->
            while (binding.consumeClick())
                keys.mutableForEach { it.call() }
        }
        loop()
    }

    val clientStringData = mutableMapOf<String, String>()
    val clientNumberData = mutableMapOf<String, Double>()
    val clientItemStackData = mutableMapOf<String, ItemStack>()
}