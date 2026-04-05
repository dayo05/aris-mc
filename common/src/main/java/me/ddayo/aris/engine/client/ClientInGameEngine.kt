package me.ddayo.aris.engine.client

import me.ddayo.aris.client.KeyBindingHelper
import me.ddayo.aris.engine.AbstractPersistentEngineCompanion
import me.ddayo.aris.engine.hook.LuaHook
import me.ddayo.aris.lua.glue.ClientInGameOnlyGenerated
import me.ddayo.aris.luagen.LuaFunc
import me.ddayo.aris.util.ListExtensions.mutableForEach
import net.minecraft.client.KeyMapping
import net.minecraft.world.item.ItemStack
import party.iroiro.luajava.Lua
import java.io.File

class ClientInGameEngine private constructor(lua: Lua) : ClientMainEngine(lua) {
    companion object: AbstractPersistentEngineCompanion<ClientInGameEngine>() {
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