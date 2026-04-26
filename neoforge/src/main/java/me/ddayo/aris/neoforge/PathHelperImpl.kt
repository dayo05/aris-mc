package me.ddayo.aris.neoforge

import net.neoforged.fml.loading.FMLPaths
import java.io.File

object PathHelperImpl {
    @JvmStatic
    fun baseDirectory(): File = FMLPaths.GAMEDIR.get().toFile()
}