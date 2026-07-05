package me.ddayo.aris.client.gui.element

import me.ddayo.aris.client.gui.BaseComponent
import me.ddayo.aris.client.gui.FontResource
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
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.util.StringUtil
import org.lwjgl.glfw.GLFW
import kotlin.math.max
import kotlin.math.min

/**
 * An editable single-line text field. The component occupies a [width] x [height] box
 * (in the parent's coordinate space) that captures clicks for focus and cursor placement,
 * and keyboard/character events while focused. Supports selection, clipboard (copy/cut/paste),
 * word-wise navigation and horizontal scrolling.
 */
@LuaProvider(ClientMainEngine.PROVIDER)
class ScriptTextInputRenderer(
    /**
     * Font to use for rendering the text.
     */
    @LuaProperty
    var font: FontResource,
    text: String,
    /**
     * The color of the text (RGB, optionally ARGB).
     */
    @LuaProperty
    var color: Int
) : BaseComponent(), IKeyboardHandlerElement, IMouseHandlerElement,
    ILuaStaticDecl by LuaClientOnlyGenerated.ScriptTextInputRenderer_LuaGenerated {
    override val isScaleRateFixed = true

    /**
     * The current text content. Setting this from Lua clamps the cursor/selection.
     */
    @LuaProperty
    var text: String = text
        set(value) {
            field = value
            cursorPos = cursorPos.coerceIn(0, value.length)
            selectionPos = selectionPos.coerceIn(0, value.length)
        }

    /**
     * The width of the input box.
     */
    @LuaProperty("width")
    var width = 100.0

    /**
     * The height of the input box.
     */
    @LuaProperty("height")
    var height = 20.0

    /**
     * Horizontal/vertical inner padding between the box border and the text.
     */
    @LuaProperty("padding")
    var padding = 4.0

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
     * Border color (ARGB) used when the field is not focused.
     */
    @LuaProperty("border_color")
    var borderColor = 0xFFA0A0A0L

    /**
     * Border color (ARGB) used when the field is focused.
     */
    @LuaProperty("focused_border_color")
    var focusedBorderColor = 0xFFFFFFFFL

    /**
     * Color (ARGB) of the text selection highlight.
     */
    @LuaProperty("selection_color")
    var selectionColor = 0x803030C0L

    /**
     * Placeholder text shown while the field is empty.
     */
    @LuaProperty("placeholder")
    var placeholder = ""

    /**
     * Color of the placeholder text.
     */
    @LuaProperty("placeholder_color")
    var placeholderColor = 0xFF808080.toInt()

    /**
     * The maximum number of characters allowed.
     */
    @LuaProperty("max_length")
    var maxLength = 256

    /**
     * Scale factor applied to the rendered text (the font is otherwise drawn at its native ~9px size).
     */
    @LuaProperty("text_scale")
    var textScale = 2.0

    /**
     * Whether the field is editable. When false, the field can still be focused but ignores edits.
     */
    @LuaProperty("is_editable")
    var isEditable = true

    /**
     * Whether the field currently has keyboard focus. Read-only in Lua; use `set_focused`.
     */
    @LuaProperty("is_focused", exportPropertySetter = false)
    var isFocused = false
        private set

    private var cursorPos = text.length
    private var selectionPos = text.length
    private var displayPos = 0
    private var lastCursorMoveTime = System.currentTimeMillis()

    private var changeHook: ((String) -> Unit)? = null
    private var enterHook: ((String) -> Unit)? = null
    private var focusHook: ((Boolean) -> Unit)? = null

    private val mcFont get() = font.font

    /**
     * Sets the focus state programmatically.
     */
    @LuaFunction("set_focused")
    fun setFocused(focused: Boolean) {
        if (isFocused == focused) return
        isFocused = focused
        resetBlink()
        focusHook?.invoke(focused)
    }

    /**
     * Returns the currently selected substring (empty if there is no selection).
     */
    @LuaFunction("get_selected_text")
    fun getSelectedText() = text.substring(min(cursorPos, selectionPos), max(cursorPos, selectionPos))

    /**
     * Sets the Lua function called whenever the text content changes.
     * The callback receives the new text (String).
     */
    @LuaFunction("set_change_hook")
    fun setChangeHook(
        @LuaCallback(params = [LuaCallbackParam("text", luaType = LuaType.STRING)])
        fn: LuaFunc
    ) { changeHook = { fn.call(it) } }

    /**
     * Removes the currently assigned change hook.
     */
    @LuaFunction("clear_change_hook")
    fun clearChangeHook() { changeHook = null }

    /**
     * Sets the Lua function called when Enter is pressed while focused.
     * The callback receives the current text (String).
     */
    @LuaFunction("set_enter_hook")
    fun setEnterHook(
        @LuaCallback(params = [LuaCallbackParam("text", luaType = LuaType.STRING)])
        fn: LuaFunc
    ) { enterHook = { fn.call(it) } }

    /**
     * Removes the currently assigned enter hook.
     */
    @LuaFunction("clear_enter_hook")
    fun clearEnterHook() { enterHook = null }

    /**
     * Sets the Lua function called when focus is gained or lost.
     * The callback receives the new focus state (Boolean).
     */
    @LuaFunction("set_focus_hook")
    fun setFocusHook(
        @LuaCallback(params = [LuaCallbackParam("focused", luaType = LuaType.BOOLEAN)])
        fn: LuaFunc
    ) { focusHook = { fn.call(it) } }

    /**
     * Removes the currently assigned focus hook.
     */
    @LuaFunction("clear_focus_hook")
    fun clearFocusHook() { focusHook = null }

    private fun resetBlink() {
        lastCursorMoveTime = System.currentTimeMillis()
    }

    private fun moveCursorTo(pos: Int, keepSelection: Boolean) {
        cursorPos = pos.coerceIn(0, text.length)
        if (!keepSelection) selectionPos = cursorPos
        resetBlink()
    }

    private fun wordBoundary(forward: Boolean): Int {
        var pos = cursorPos
        if (forward) {
            while (pos < text.length && text[pos] == ' ') pos++
            while (pos < text.length && text[pos] != ' ') pos++
        } else {
            while (pos > 0 && text[pos - 1] == ' ') pos--
            while (pos > 0 && text[pos - 1] != ' ') pos--
        }
        return pos
    }

    private fun insertText(insertion: String) {
        if (!isEditable) return
        val start = min(cursorPos, selectionPos)
        val end = max(cursorPos, selectionPos)
        val budget = (maxLength - (text.length - (end - start))).coerceAtLeast(0)
        var filtered = StringUtil.filterText(insertion)
        if (filtered.length > budget) filtered = filtered.substring(0, budget)
        if (filtered.isEmpty() && start == end) return
        text = StringBuilder(text).replace(start, end, filtered).toString()
        moveCursorTo(start + filtered.length, false)
        changeHook?.invoke(text)
    }

    private fun deleteSelection(): Boolean {
        if (cursorPos == selectionPos) return false
        insertText("")
        return true
    }

    private fun deleteChars(forward: Boolean) {
        if (!isEditable) return
        if (deleteSelection()) return
        if (forward) {
            if (cursorPos >= text.length) return
            text = StringBuilder(text).deleteCharAt(cursorPos).toString()
        } else {
            if (cursorPos <= 0) return
            val newPos = cursorPos - 1
            text = StringBuilder(text).deleteCharAt(newPos).toString()
            moveCursorTo(newPos, false)
        }
        resetBlink()
        changeHook?.invoke(text)
    }

    override fun mouseDown(mx: Double, my: Double, button: Int): Boolean {
        val inside = isVisible && isActive && mx in 0.0..width && my in 0.0..height
        if (!inside) {
            setFocused(false)
            return false
        }
        setFocused(true)
        if (button == 0) {
            val nativeInnerWidth = ((width - 2 * padding) / textScale).toInt().coerceAtLeast(0)
            val nativeRelX = ((mx - padding) / textScale).toInt().coerceIn(0, nativeInnerWidth)
            val clicked = mcFont.plainSubstrByWidth(text.substring(displayPos.coerceIn(0, text.length)), nativeRelX)
            moveCursorTo(displayPos + clicked.length, Screen.hasShiftDown())
        }
        return true
    }

    override fun mouseUp(mx: Double, my: Double, button: Int) = false

    override fun keyDown(keyCode: Int, scanCode: Int, modifier: Int): Boolean {
        if (!isFocused || !isVisible || !isActive) return false
        val shift = Screen.hasShiftDown()
        val ctrl = Screen.hasControlDown()

        if (Screen.isSelectAll(keyCode)) {
            selectionPos = 0
            moveCursorTo(text.length, true)
            return true
        }
        if (Screen.isCopy(keyCode)) {
            Minecraft.getInstance().keyboardHandler.clipboard = getSelectedText()
            return true
        }
        if (Screen.isPaste(keyCode)) {
            insertText(Minecraft.getInstance().keyboardHandler.clipboard)
            return true
        }
        if (Screen.isCut(keyCode)) {
            Minecraft.getInstance().keyboardHandler.clipboard = getSelectedText()
            deleteSelection()
            return true
        }

        return when (keyCode) {
            GLFW.GLFW_KEY_BACKSPACE -> { deleteChars(false); true }
            GLFW.GLFW_KEY_DELETE -> { deleteChars(true); true }
            GLFW.GLFW_KEY_LEFT -> { moveCursorTo(if (ctrl) wordBoundary(false) else cursorPos - 1, shift); true }
            GLFW.GLFW_KEY_RIGHT -> { moveCursorTo(if (ctrl) wordBoundary(true) else cursorPos + 1, shift); true }
            GLFW.GLFW_KEY_HOME -> { moveCursorTo(0, shift); true }
            GLFW.GLFW_KEY_END -> { moveCursorTo(text.length, shift); true }
            GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> { enterHook?.invoke(text); true }
            else -> false
        }
    }

    override fun keyUp(keyCode: Int, scanCode: Int, modifier: Int) = false

    override fun charTyped(codePoint: Char, modifier: Int): Boolean {
        if (!isFocused || !isVisible || !isActive) return false
        if (!StringUtil.isAllowedChatCharacter(codePoint)) return false
        insertText(codePoint.toString())
        return true
    }

    /**
     * Adjusts [displayPos] so that the cursor stays within the visible area. Mirrors vanilla EditBox.
     * [nativeInnerWidth] is the available width expressed in native (un-scaled) font pixels.
     */
    private fun scroll(nativeInnerWidth: Int) {
        displayPos = displayPos.coerceIn(0, text.length)
        val visible = mcFont.plainSubstrByWidth(text.substring(displayPos), nativeInnerWidth)
        val visibleEnd = visible.length + displayPos
        if (cursorPos == displayPos)
            displayPos -= mcFont.plainSubstrByWidth(text, nativeInnerWidth, true).length
        if (cursorPos > visibleEnd) displayPos += cursorPos - visibleEnd
        else if (cursorPos <= displayPos) displayPos -= displayPos - cursorPos
        displayPos = displayPos.coerceIn(0, text.length)
    }

    private fun RenderUtil.fillColor(c: Long, fx: Double, fy: Double, fw: Double, fh: Double) {
        val a = ((c ushr 24) and 0xff).toInt()
        if (a == 0 || fw <= 0.0 || fh <= 0.0) return
        val r = ((c ushr 16) and 0xff).toInt()
        val g = ((c ushr 8) and 0xff).toInt()
        val b = (c and 0xff).toInt()
        fillRender(fx, fy, fw, fh, r, g, b, a)
    }

    override fun RenderUtil._render(mx: Double, my: Double, delta: Float) {
        val f = mcFont
        fillColor(backgroundColor, 0.0, 0.0, width, height)
        if (borderWidth > 0.0) {
            val bc = if (isFocused) focusedBorderColor else borderColor
            fillColor(bc, 0.0, 0.0, width, borderWidth)
            fillColor(bc, 0.0, height - borderWidth, width, borderWidth)
            fillColor(bc, 0.0, 0.0, borderWidth, height)
            fillColor(bc, width - borderWidth, 0.0, borderWidth, height)
        }

        val nativeInnerWidth = ((width - 2 * padding) / textScale).toInt().coerceAtLeast(0)
        scroll(nativeInnerWidth)

        val textX = padding
        val textY = (height - f.lineHeight * textScale) / 2

        // Render the text/selection/cursor inside a scaled matrix so the font fills the box.
        // Inside this block all coordinates are in native (un-scaled) font pixels.
        push {
            translate(textX, textY, 0.0)
            scale(textScale, textScale, 1.0)

            if (text.isEmpty()) {
                if (placeholder.isNotEmpty())
                    graphics.drawString(f, placeholder, 0, 0, placeholderColor)
            } else {
                val displayStart = displayPos.coerceIn(0, text.length)
                val displayText = f.plainSubstrByWidth(text.substring(displayStart), nativeInnerWidth)
                val displayEnd = displayStart + displayText.length

                // selection highlight
                val selStart = min(cursorPos, selectionPos)
                val selEnd = max(cursorPos, selectionPos)
                val visSelStart = selStart.coerceIn(displayStart, displayEnd)
                val visSelEnd = selEnd.coerceIn(displayStart, displayEnd)
                if (isFocused && visSelEnd > visSelStart) {
                    val sx = f.width(text.substring(displayStart, visSelStart)).toDouble()
                    val ex = f.width(text.substring(displayStart, visSelEnd)).toDouble()
                    fillColor(selectionColor, sx, -1.0, ex - sx, f.lineHeight + 2.0)
                }

                graphics.drawString(f, displayText, 0, 0, color)

                // cursor
                if (isFocused && cursorBlinkVisible() && cursorPos in displayStart..displayEnd) {
                    val cx = f.width(text.substring(displayStart, cursorPos)).toDouble()
                    fillColor(0xFFE0E0E0L, cx, -1.0, 1.0, f.lineHeight + 1.0)
                }
            }
        }
    }

    private fun cursorBlinkVisible() = ((System.currentTimeMillis() - lastCursorMoveTime) / 500) % 2 == 0L
}
