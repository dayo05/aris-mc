package me.ddayo.aris.engine.client

import me.ddayo.aris.engine.InitEngine
import me.ddayo.aris.lua.glue.ClientInitGenerated
import me.ddayo.aris.particle.ParticleInfo
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.resources.ResourceLocation
import party.iroiro.luajava.Lua
import java.io.File

@Environment(EnvType.CLIENT)
class ClientInitEngine(lua: Lua): InitEngine(lua) {
    companion object {
        const val PROVIDER = "ClientInitGenerated"
        var INSTANCE: ClientInitEngine? = null
            private set
    }

    init {
        if(INSTANCE!=null) throw IllegalStateException("Client Init Engine cannot be initialized twice")
        INSTANCE = this
        ClientInitGenerated.initEngine(this)
        ClientEngineAddOn.clientInitEngineAddOns().forEach {
            it.initLua(this)
        }

        File("robots/client-init").listFiles()?.forEach {
            createTask(it, it.nameWithoutExtension)
        }
    }

    override val basePath = File("robots/client-init")
    val particleInfo = mutableMapOf<ResourceLocation, ParticleInfo>()
}