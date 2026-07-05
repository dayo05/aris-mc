package me.ddayo.aris.client.gui.element

import me.ddayo.aris.client.gui.BaseComponent
import me.ddayo.aris.client.gui.RenderUtil
import me.ddayo.aris.engine.client.ClientMainEngine
import me.ddayo.aris.lua.glue.LuaClientOnlyGenerated
import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.luagen.LuaCallback
import me.ddayo.aris.luagen.LuaCallbackParam
import me.ddayo.aris.luagen.LuaFunc
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProperty
import me.ddayo.aris.luagen.LuaProvider
import me.ddayo.aris.luagen.LuaType

/**
 * A square checkbox that toggles its [isChecked] state when clicked.
 * Occupies a [size] x [size] box (in the parent's coordinate space) that captures clicks.
 */
@LuaProvider(ClientMainEngine.PROVIDER)
class ScriptCheckboxRenderer(
    checked: Boolean
) : BaseComponent(), IMouseHandlerElement,
    ILuaStaticDecl by LuaClientOnlyGenerated.ScriptCheckboxRenderer_LuaGenerated {
    override val isScaleRateFixed = true

    /**
     * The side length of the checkbox box.
     */
    @LuaProperty("size")
    var size = 16.0

    /**
     * Thickness of the box border. Set to 0 to disable.
     */
    @LuaProperty("border_width")
    var borderWidth = 1.0

    /**
     * Background fill color (ARGB). Alpha 0 disables the background.
     */
    @LuaProperty("background_color")
    var backgroundColor = 0xFF000000L

    /**
     * Border color (ARGB).
     */
    @LuaProperty("border_color")
    var borderColor = 0xFFA0A0A0L

    /**
     * Color (ARGB) of the check mark drawn when checked.
     */
    @LuaProperty("check_color")
    var checkColor = 0xFF40E040L

    /**
     * Whether the checkbox is currently checked.
     */
    @LuaProperty("is_checked")
    var isChecked = checked

    private var changeHook: ((Boolean) -> Unit)? = null

    /**
     * Sets the Lua function called when the checked state is toggled by a click.
     * The callback receives the new checked state (Boolean).
     */
    @LuaFunction("set_change_hook")
    fun setChangeHook(
        @LuaCallback(params = [LuaCallbackParam("checked", luaType = LuaType.BOOLEAN)])
        fn: LuaFunc
    ) { changeHook = { fn.call(it) } }

    /**
     * Removes the currently assigned change hook.
     */
    @LuaFunction("clear_change_hook")
    fun clearChangeHook() { changeHook = null }

    override fun mouseDown(mx: Double, my: Double, button: Int): Boolean {
        if (!isVisible || !isActive || button != 0) return false
        if (mx in 0.0..size && my in 0.0..size) {
            isChecked = !isChecked
            changeHook?.invoke(isChecked)
            return true
        }
        return false
    }

    override fun mouseUp(mx: Double, my: Double, button: Int) = false

    private fun RenderUtil.fillColor(c: Long, fx: Double, fy: Double, fw: Double, fh: Double) {
        val a = ((c ushr 24) and 0xff).toInt()
        if (a == 0 || fw <= 0.0 || fh <= 0.0) return
        val r = ((c ushr 16) and 0xff).toInt()
        val g = ((c ushr 8) and 0xff).toInt()
        val b = (c and 0xff).toInt()
        fillRender(fx, fy, fw, fh, r, g, b, a)
    }

    override fun RenderUtil._render(mx: Double, my: Double, delta: Float) {
        fillColor(backgroundColor, 0.0, 0.0, size, size)
        if (borderWidth > 0.0) {
            fillColor(borderColor, 0.0, 0.0, size, borderWidth)
            fillColor(borderColor, 0.0, size - borderWidth, size, borderWidth)
            fillColor(borderColor, 0.0, 0.0, borderWidth, size)
            fillColor(borderColor, size - borderWidth, 0.0, borderWidth, size)
        }
        if (isChecked) {
            // filled inner square, inset from the border
            val inset = (size * 0.25).coerceAtLeast(borderWidth + 1.0)
            fillColor(checkColor, inset, inset, size - 2 * inset, size - 2 * inset)
        }
    }
}
