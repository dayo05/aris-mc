package me.ddayo.aris.engine.fabric

import net.fabricmc.loader.api.FabricLoader
import java.io.File

object PathHelperImpl {
    @JvmStatic
    fun baseDirectory(): File = FabricLoader.getInstance().gameDir.toFile()
}