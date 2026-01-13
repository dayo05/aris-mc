package me.ddayo.aris.client.gui

import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.luagen.LuaFunc
import me.ddayo.aris.engine.client.ClientMainEngine
import me.ddayo.aris.client.gui.element.IClickableElement
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import me.ddayo.aris.lua.glue.LuaClientOnlyGenerated
import me.ddayo.aris.luagen.LuaProperty
import me.ddayo.aris.util.ListExtensions.mutableForEach
import kotlin.math.cos
import kotlin.math.sin

@LuaProvider(ClientMainEngine.PROVIDER)
open class BaseComponent : ILuaStaticDecl by LuaClientOnlyGenerated.BaseComponent_LuaGenerated {
    /**
     * The X coordinate of the component's position.
     */
    @LuaProperty
    var x = 0.0

    /**
     * The Y coordinate of the component's position.
     */
    @LuaProperty
    var y = 0.0

    /**
     * The rotation of the component in degrees.
     */
    @LuaProperty
    var rotation = 0.0

    /**
     * The X coordinate of the anchor point (pivot) relative to the component's origin.
     * Rotation and scaling occur around this point.
     */
    @LuaProperty("anchor_x")
    var anchorX = 0.0

    /**
     * The Y coordinate of the anchor point (pivot) relative to the component's origin.
     * Rotation and scaling occur around this point.
     */
    @LuaProperty("anchor_y")
    var anchorY = 0.0

    /**
     * Determines if the scale rate is locked.
     * If true, [xScale] and [yScale] cannot be modified individually.
     */
    @LuaProperty(name = "is_scale_rate_fixed")
    open val isScaleRateFixed = false

    /**
     * The scale factor on the X axis.
     * @throws AssertionError if [isScaleRateFixed] is true.
     */
    @LuaProperty(name = "scale_x")
    var xScale = 1.0
        set(value) {
            assert(!isScaleRateFixed)
            field = value
        }

    /**
     * The scale factor on the Y axis.
     * @throws AssertionError if [isScaleRateFixed] is true.
     */
    @LuaProperty(name = "scale_y")
    var yScale = 1.0
        set(value) {
            assert(!isScaleRateFixed)
            field = value
        }

    /**
     * A utility property to set or get uniform scaling for both X and Y axes.
     * Getting this property asserts that [xScale] equals [yScale].
     */
    @LuaProperty(name = "scale")
    var scale: Double
        get() = run {
            assert(xScale == yScale)
            return xScale
        }
        set(value) {
            xScale = value
            yScale = value
        }

    /**
     * Controls whether the component is rendered.
     */
    @LuaProperty(name = "is_visible")
    var isVisible = true

    /**
     * Controls whether the component accepts interactions (like clicks).
     */
    @LuaProperty(name = "is_active")
    var isActive = true

    fun tick() {
        if(!isActive) return

        addedWidgets.mutableForEach {
            it.tick()
        }
    }

    open fun render(r: RenderUtil, mx: Double, my: Double, delta: Float) {
        if (!isVisible) return
        r.apply {
            push {
                translate(x, y, 0.0)
                rotate(0.0, 0.0, Math.toRadians(rotation))
                scale(xScale, yScale, 1.0)
                translate(-anchorX, -anchorY, 0.0)

                val (nmx, nmy) = getLocalMouse(mx, my)

                renderHooks.mutableForEach {
                    it.call(nmx, nmy, delta)
                }
                _render(nmx, nmy, delta)
                addedWidgets.mutableForEach {
                    it.render(r, nmx, nmy, delta)
                }
            }
        }
    }

    protected val addedWidgets = mutableListOf<BaseComponent>()

    /**
     * Adds a child component to this component.
     * The child moves relative to this parent.
     *
     * @param child The component to add.
     */
    @LuaFunction(name = "add_child")
    fun addChild(child: BaseComponent) {
        addedWidgets.add(child)
        child.parent = this
    }

    private val renderHooks = mutableListOf<LuaFunc>()
    private val tickHooks = mutableListOf<LuaFunc>()

    /**
     * Adds a hook function to be invoked on each frame during rendering.
     *
     * @param fn The Lua function to call. (scaled_mouse_x, scaled_mouse_y, tick_delta) -> void
     */
    @LuaFunction(name = "add_render_hook")
    fun addRenderHook(fn: LuaFunc) {
        renderHooks.add(fn)
    }

    /**
     * Clears all registered render hooks.
     */
    @LuaFunction(name = "clear_render_hook")
    fun clearRenderHook() {
        renderHooks.clear()
    }

    @LuaFunction(name = "add_tick_hook")
    fun addTickHook(fn: LuaFunc) {
        tickHooks.add(fn)
    }

    @LuaFunction(name = "clear_tick_hook")
    fun clearTickHook() {
        tickHooks.clear()
    }

    /**
     * Removes all child components from this component.
     */
    @LuaFunction(name = "clear_child")
    fun clearChild() {
        addedWidgets.forEach {
            it.parent = null
        }
        addedWidgets.clear()
    }

    /**
     * Removes a specific child component.
     *
     * @param child The component to remove.
     */
    @LuaFunction(name = "remove_child")
    fun removeChild(child: BaseComponent) {
        if(addedWidgets.remove(child))
            child.parent = null
    }

    open fun RenderUtil._render(mx: Double, my: Double, delta: Float) {}

    fun onMouseRelease(mx: Double, my: Double, button: Int): Boolean {
        if (!isVisible || !isActive) return false
        val (nmx, nmy) = getLocalMouse(mx, my)
        if (this is IClickableElement)
            if(clicked(nmx, nmy, button)) return true
        addedWidgets.mutableForEach {
            if(it.onMouseRelease(nmx, nmy, button)) return true
        }
        return false
    }

    private fun getLocalMouse(mx: Double, my: Double): Pair<Double, Double> {
        val dx = mx - x
        val dy = my - y

        val (rdx, rdy) = if (rotation == 0.0) {
            dx to dy
        } else {
            val rad = Math.toRadians(-rotation)
            val c = cos(rad)
            val s = sin(rad)
            (dx * c - dy * s) to (dx * s + dy * c)
        }

        return ((rdx / xScale) + anchorX) to ((rdy / yScale) + anchorY)
    }

    /**
     * The parent component of this component, or null if it has no parent.
     * This property cannot be set from Lua.
     */
    @LuaProperty(exportPropertySetter = false)
    var parent: BaseComponent? = null
}