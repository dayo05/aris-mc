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
    @LuaProperty
    var x = 0.0

    @LuaProperty
    var y = 0.0

    @LuaProperty
    var rotation = 0.0

    @LuaProperty("anchor_x")
    var anchorX = 0.0

    @LuaProperty("anchor_y")
    var anchorY = 0.0

    @LuaProperty(name = "is_scale_rate_fixed")
    open val isScaleRateFixed = false

    @LuaProperty(name = "scale_x")
    var xScale = 1.0
        set(value) {
            assert(!isScaleRateFixed)
            field = value
        }

    @LuaProperty(name = "scale_y")
    var yScale = 1.0
        set(value) {
            assert(!isScaleRateFixed)
            field = value
        }

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

    @LuaProperty(name = "is_visible")
    var isVisible = true

    @LuaProperty(name = "is_active")
    var isActive = true

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

    @LuaFunction(name = "add_child")
    fun addChild(child: BaseComponent) {
        addedWidgets.add(child)
        child.parent = this
    }

    private val renderHooks = mutableListOf<LuaFunc>()

    /**
     * The Function invoked on each frame
     */
    @LuaFunction(name = "add_render_hook")
    fun addRenderHook(fn: LuaFunc) {
        renderHooks.add(fn)
    }

    @LuaFunction(name = "clear_render_hook")
    fun clearRenderHook() {
        renderHooks.clear()
    }

    @LuaFunction(name = "clear_child")
    fun clearChild() {
        addedWidgets.forEach {
            it.parent = null
        }
        addedWidgets.clear()
    }

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

    @LuaProperty(exportPropertySetter = false)
    var parent: BaseComponent? = null
}