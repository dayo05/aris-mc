package me.ddayo.aris.engine.client

import me.ddayo.aris.engine.AbstractPersistentEngineCompanion
import me.ddayo.aris.engine.MCBaseEngine
import me.ddayo.aris.lua.glue.InGameGenerated
import me.ddayo.aris.lua.glue.LuaClientOnlyGenerated
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import party.iroiro.luajava.Lua
import java.io.File

@Environment(EnvType.CLIENT)
open class ClientMainEngine protected constructor(lua: Lua) : MCBaseEngine(lua) {
    companion object: AbstractPersistentEngineCompanion<ClientMainEngine>() {
        const val PROVIDER = "LuaClientOnlyGenerated"
        override val searchPath = "robots/client"

        override fun _createEngine(lua: Lua) = ClientMainEngine(lua)
    }

    override val basePath = File(searchPath)

    init {
        InGameGenerated.initEngine(this)
        LuaClientOnlyGenerated.initEngine(this)
        ClientEngineAddOn.clientMainEngineAddOns().forEach {
            it.initLua(this)
        }
    }
}