package me.ddayo.aris.client.gui.element

import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.engine.client.ClientMainEngine
import me.ddayo.aris.client.gui.BaseComponent
import me.ddayo.aris.lua.glue.LuaClientOnlyGenerated
import me.ddayo.aris.luagen.LuaFunc
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.math.Area
import me.ddayo.aris.math.Point.Companion.with
import me.ddayo.aris.luagen.LuaProperty
import me.ddayo.aris.luagen.LuaProvider

@LuaProvider(ClientMainEngine.PROVIDER)
class ScriptMouseHandlerRenderer(
    private var clickHook: ((Double, Double, Int) -> Unit)?,
    /**
     * The area within which clicks are detected.
     */
    @LuaProperty
    var area: Area
) : ILuaStaticDecl by LuaClientOnlyGenerated.ScriptMouseHandlerRenderer_LuaGenerated, BaseComponent(), IMouseHandlerElement {
    override val isScaleRateFixed = true
    override fun mouseUp(mx: Double, my: Double, button: Int) = if(area.isIn(mx with my) && isActive && isVisible) {
        clickHook?.let {
            it.invoke(mx, my, button)
            true
        } ?: false
    }
    else false

    override fun mouseDown(mx: Double, my: Double, button: Int) = if(area.isIn(mx with my) && isActive && isVisible) {
        mouseDownHook?.let {
            it.invoke(mx, my, button)
            true
        } ?: false
    } else false

    override fun mouseDrag(mx: Double, my: Double, button: Int) = if(area.isIn(mx with my) && isActive && isVisible) {
        mouseDragHook?.let {
            it.invoke(mx, my, button)
            true
        } ?: false
    } else false

    /**
     * Sets the Lua function to be called when a mouse button is released (mouse up) within the component's area.
     *
     * The Lua function will receive the following arguments:
     * 1. `mx` (Double): The X coordinate of the mouse cursor.
     * 2. `my` (Double): The Y coordinate of the mouse cursor.
     * 3. `button` (Int): The index of the mouse button pressed.
     *
     * @param fn The Lua function to serve as the callback.
     */
    @LuaFunction(name = "set_mouse_up_hook")
    fun setMouseUpHook(fn: LuaFunc) {
        clickHook = fn::call
    }

    /**
     * Removes the currently assigned mouse up hook, disabling the callback.
     */
    @LuaFunction(name = "clear_mouse_up_hook")
    fun clearMouseUpHook() {
        clickHook = null
    }

    private var mouseDownHook: ((Double, Double, Int) -> Unit)? = null

    /**
     * Sets the Lua function to be called when a mouse button is pressed (mouse down) within the component's area.
     *
     * The Lua function will receive the following arguments:
     * 1. `mx` (Double): The X coordinate of the mouse cursor.
     * 2. `my` (Double): The Y coordinate of the mouse cursor.
     * 3. `button` (Int): The index of the mouse button pressed.
     *
     * @param fn The Lua function to serve as the callback.
     */
    @LuaFunction(name = "set_mouse_down_hook")
    fun setMouseDownHook(fn: LuaFunc) {
        mouseDownHook = fn::call
    }

    /**
     * Removes the currently assigned mouse down hook, disabling the callback.
     */
    @LuaFunction(name = "clear_mouse_down_hook")
    fun clearMouseDownHook() { mouseDownHook = null }

    private var mouseDragHook: ((Double, Double, Int) -> Unit)? = null

    /**
     * Sets the Lua function to be called when the mouse is dragged within the component's area.
     *
     * The Lua function will receive the following arguments:
     * 1. `mx` (Double): The X coordinate of the mouse cursor.
     * 2. `my` (Double): The Y coordinate of the mouse cursor.
     * 3. `button` (Int): The index of the mouse button being held.
     *
     * @param fn The Lua function to serve as the callback.
     */
    @LuaFunction(name = "set_mouse_drag_hook")
    fun setMouseDragHook(fn: LuaFunc) {
        mouseDragHook = fn::call
    }

    /**
     * Removes the currently assigned mouse drag hook, disabling the callback.
     */
    @LuaFunction(name = "clear_mouse_drag_hook")
    fun clearMouseDragHook() { mouseDragHook = null }
}