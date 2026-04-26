package me.ddayo.aris.engine

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import com.moandjiezana.toml.Toml
import me.ddayo.aris.luagen.LuaEngine
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import me.ddayo.aris.luagen.RetrieveEngine
import org.yaml.snakeyaml.Yaml
import party.iroiro.luajava.Lua
import party.iroiro.luajava.value.LuaValue
import java.util.Date

@LuaProvider(library = "aris.parse")
object ParseFunction {
    /**
     * Parse a JSON string into a Lua table.
     */
    @LuaFunction("from_json")
    fun parseJson(@RetrieveEngine engine: LuaEngine, content: String): LuaValue {
        val lua = engine.lua
        return jsonToLua(lua, JsonParser.parseString(content))
    }

    /**
     * Parse a YAML string into a Lua table.
     */
    @LuaFunction("from_yaml")
    fun parseYaml(@RetrieveEngine engine: LuaEngine, content: String): LuaValue {
        val lua = engine.lua
        return anyToLua(lua, Yaml().load(content))
    }

    /**
     * Parse a TOML string into a Lua table.
     */
    @LuaFunction("from_toml")
    fun parseToml(@RetrieveEngine engine: LuaEngine, content: String): LuaValue {
        val lua = engine.lua
        return anyToLua(lua, Toml().read(content).toMap())
    }

    private fun nil(lua: Lua): LuaValue {
        lua.pushNil()
        return lua.get()
    }

    private fun newTable(lua: Lua): LuaValue {
        lua.newTable()
        return lua.get()
    }

    private fun jsonToLua(lua: Lua, element: JsonElement?): LuaValue {
        return when (element) {
            null, is JsonNull -> nil(lua)
            is JsonPrimitive -> when {
                element.isBoolean -> lua.from(element.asBoolean)
                element.isNumber -> {
                    val n = element.asNumber
                    val d = n.toDouble()
                    if (d % 1.0 == 0.0 && d >= Long.MIN_VALUE.toDouble() && d <= Long.MAX_VALUE.toDouble())
                        lua.from(n.toLong())
                    else lua.from(d)
                }
                element.isString -> lua.from(element.asString)
                else -> nil(lua)
            }
            is JsonArray -> {
                val table = newTable(lua)
                element.forEachIndexed { i, v -> table.set(i + 1, jsonToLua(lua, v)) }
                table
            }
            is JsonObject -> {
                val table = newTable(lua)
                for ((k, v) in element.entrySet()) table.set(k, jsonToLua(lua, v))
                table
            }
            else -> nil(lua)
        }
    }

    private fun anyToLua(lua: Lua, value: Any?): LuaValue {
        return when (value) {
            null -> nil(lua)
            is Boolean -> lua.from(value)
            is Int -> lua.from(value.toLong())
            is Long -> lua.from(value)
            is Short -> lua.from(value.toLong())
            is Byte -> lua.from(value.toLong())
            is Float -> lua.from(value.toDouble())
            is Double -> lua.from(value)
            is Number -> lua.from(value.toDouble())
            is String -> lua.from(value)
            is Date -> lua.from(value.toInstant().toString())
            is Map<*, *> -> {
                val table = newTable(lua)
                for ((k, v) in value) table.set(k.toString(), anyToLua(lua, v))
                table
            }
            is List<*> -> {
                val table = newTable(lua)
                value.forEachIndexed { i, v -> table.set(i + 1, anyToLua(lua, v)) }
                table
            }
            is Array<*> -> {
                val table = newTable(lua)
                value.forEachIndexed { i, v -> table.set(i + 1, anyToLua(lua, v)) }
                table
            }
            else -> lua.from(value.toString())
        }
    }
}