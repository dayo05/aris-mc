package me.ddayo.aris.client.gui

import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.engine.client.ClientMainEngine
import me.ddayo.aris.lua.glue.LuaClientOnlyGenerated
import me.ddayo.aris.luagen.LuaProperty
import me.ddayo.aris.luagen.LuaProvider

@LuaProvider(ClientMainEngine.PROVIDER)
open class BaseRectComponent : BaseComponent(),
    ILuaStaticDecl by LuaClientOnlyGenerated.BaseRectComponent_LuaGenerated {
    /**
     * The visual width of the component.
     * Setting this updates [xScale] based on [fixedWidth].
     */
    @LuaProperty("width")
    var width = 1.0
        set(value) {
            xScale = value / fixedWidth
            field = value
        }

    /**
     * The visual height of the component.
     * Setting this updates [yScale] based on [fixedHeight].
     */
    @LuaProperty("height")
    var height = 1.0
        set(value) {
            yScale = value / fixedHeight
            field = value
        }

    /**
     * The internal reference width (e.g., texture width).
     * Changing this recalculates [xScale] to maintain visual [width].
     */
    @LuaProperty("fixed_width")
    var fixedWidth = 1.0
        set(value) {
            xScale = width / value
            field = value
        }

    /**
     * The internal reference height (e.g., texture height).
     * Changing this recalculates [yScale] to maintain visual [height].
     */
    @LuaProperty("fixed_height")
    var fixedHeight = 1.0
        set(value) {
            yScale = height / value
            field = value
        }
}