package me.ddayo.aris.engine

import dev.architectury.injectables.annotations.ExpectPlatform
import java.io.File

/**
 * Resolves the base directory that FileIO operations are rooted at.
 * - On a dedicated server, this is the server's working directory.
 * - On a client (including single-player), this is the game directory (e.g. .minecraft/).
 */
object PathHelper {
    @JvmStatic
    @ExpectPlatform
    fun baseDirectory(): File {
        throw NotImplementedError()
    }

    /**
     * Resolve a script-supplied path against the base directory, forbidding escape via `..`.
     */
    fun resolve(path: String): File {
        val base = baseDirectory().canonicalFile
        val resolved = File(base, path).canonicalFile
        require(resolved.toPath().startsWith(base.toPath())) {
            "Path '$path' escapes the base directory"
        }
        return resolved
    }
}