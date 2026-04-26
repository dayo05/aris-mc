package me.ddayo.aris.engine

import me.ddayo.aris.Aris
import me.ddayo.aris.luagen.LuaEngine
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import me.ddayo.aris.luagen.RetrieveEngine
import org.apache.logging.log4j.LogManager
import party.iroiro.luajava.value.LuaValue

@LuaProvider(library = "aris")
object BaseFunction {
    private val logger = LogManager.getLogger()
    @LuaFunction("log_debug")
    fun debugLog(msg: String) = logger.debug(msg)

    @LuaFunction("log_info")
    fun infoLog(msg: String) = logger.info(msg)

    @LuaFunction("log_warn")
    fun warnLog(msg: String) = logger.warn(msg)

    @LuaFunction("log_error")
    fun errorLog(msg: String) = logger.error(msg)

    @LuaFunction("check_version")
    fun version(v: String) {
        if(Version.versionCompare(v, Aris.VERSION) > 0) throw Exception("Current script needs $v which is not capable with ARIS ${Aris.VERSION}")
    }

    /**
     * Read the entire contents of a file as a UTF-8 string.
     * Path is resolved relative to the base directory
     * (gamedir on client, server working directory on a dedicated server).
     */
    @LuaFunction("read_file")
    fun readFile(path: String): String = PathHelper.resolve(path).readText(Charsets.UTF_8)

    /**
     * Write a UTF-8 string to a file, overwriting any existing content. Parent directories are created as needed.
     */
    @LuaFunction("write_file")
    fun writeFile(path: String, content: String) {
        val file = PathHelper.resolve(path)
        file.parentFile?.mkdirs()
        file.writeText(content, Charsets.UTF_8)
    }

    /**
     * Append a UTF-8 string to a file. Parent directories are created as needed.
     */
    @LuaFunction("append_file")
    fun appendFile(path: String, content: String) {
        val file = PathHelper.resolve(path)
        file.parentFile?.mkdirs()
        file.appendText(content, Charsets.UTF_8)
    }

    /**
     * Returns true if the path exists on disk.
     */
    @LuaFunction("is_file_exists")
    fun fileExists(path: String): Boolean = PathHelper.resolve(path).exists()

    /**
     * Returns true if the path exists and is a directory.
     */
    @LuaFunction("is_directory")
    fun isDirectory(path: String): Boolean = PathHelper.resolve(path).isDirectory

    /**
     * Delete a file or (recursively) a directory. Returns true on success.
     */
    @LuaFunction("delete_file")
    fun deleteFile(path: String): Boolean = PathHelper.resolve(path).deleteRecursively()

    /**
     * Create a directory, including any required parent directories. Returns true if created.
     */
    @LuaFunction("create_directory")
    fun createDirectory(path: String): Boolean = PathHelper.resolve(path).mkdirs()

    /**
     * List the immediate children of a directory. Returns a Lua table (1-indexed) of file names.
     * Returns an empty table if the path is not a directory or is not readable.
     */
    @LuaFunction("list_files")
    fun listFiles(@RetrieveEngine engine: LuaEngine, path: String): LuaValue {
        val lua = engine.lua
        val entries = PathHelper.resolve(path).listFiles()
        lua.newTable()
        val table = lua.get()
        entries?.forEachIndexed { i, f -> table.set(i + 1, lua.from(f.name)) }
        return table
    }

    /**
     * Get the size of a file in bytes. Returns -1 if the file does not exist.
     */
    @LuaFunction("file_size")
    fun fileSize(path: String): Long {
        val file = PathHelper.resolve(path)
        return if (file.exists()) file.length() else -1L
    }
}