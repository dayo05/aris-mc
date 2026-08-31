package me.ddayo.aris.engine.neoforge

import net.neoforged.fml.loading.FMLPaths
import java.io.File

object PathHelperImpl {
    @JvmStatic
    fun baseDirectory(): File = FMLPaths.GAMEDIR.get().toFile()
}