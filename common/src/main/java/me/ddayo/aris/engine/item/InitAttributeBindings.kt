package me.ddayo.aris.engine.item

import me.ddayo.aris.engine.InitEngine

/**
 * aris.luagen 1.0-SNAPSHOT counts the receiver in instance overload dispatch, but
 * compares it to the number of explicit arguments. A one-argument colon call
 * therefore selects the two-argument native binding and corrupts the Lua stack.
 * Keep generated signatures/docs, and dispatch just these new overloads by exact
 * arity (excluding self).
 * Remove this adapter once the generator's instance overload dispatch is fixed.
 */
internal object InitAttributeBindings {
    fun install(engine: InitEngine) {
        engine.lua.load(
            """
            local function namespace_overload(one, two)
                return function(self, ...)
                    local count = select('#', ...)
                    if count == 1 then return one(self, ...) end
                    if count == 2 then return two(self, ...) end
                    error("Expected path or namespace, path", 2)
                end
            end
            local item = aris_me_ddayo_aris_engine_item_ItemAttrBuilder
            getmetatable(aris.init.item_attr()).__index.creative_tab =
                namespace_overload(item.creative_tab_kt0, item.creative_tab_kt1)
            local tab = aris_me_ddayo_aris_engine_item_CreativeTabAttrBuilder
            getmetatable(aris.init.creative_tab_attr()).__index.icon =
                namespace_overload(tab.icon_kt0, tab.icon_kt1)
            """.trimIndent()
        )
        engine.lua.pCall(0, 0)
    }
}
