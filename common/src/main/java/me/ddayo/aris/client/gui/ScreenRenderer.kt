package me.ddayo.aris.client.gui

import com.mojang.blaze3d.systems.RenderSystem
import me.ddayo.aris.client.gui.element.IKeyboardHandlerElement
import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.engine.client.ClientMainEngine
import me.ddayo.aris.lua.glue.LuaClientOnlyGenerated
import me.ddayo.aris.luagen.LuaFunc
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProperty
import me.ddayo.aris.luagen.LuaProvider
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component
import org.apache.logging.log4j.LogManager

@LuaProvider(ClientMainEngine.PROVIDER)
class ScreenRenderer : BaseRectComponent(), ILuaStaticDecl by LuaClientOnlyGenerated.ScreenRenderer_LuaGenerated {
    private var attachedScreen: Any? = null

    init {
        fixedWidth = 1920.0
        fixedHeight = 1080.0
    }

    /**
     * Determines if the screen can be closed by pressing the Escape key.
     */
    @LuaProperty("can_exit_with_esc")
    var canExitWithEsc = true

    /**
     * The actual width of the game window.
     * This property is read-only in Lua.
     */
    @LuaProperty("window_width", exportPropertySetter = false)
    var windowWidth = fixedWidth
        private set

    /**
     * The actual height of the game window.
     * This property is read-only in Lua.
     */
    @LuaProperty("window_height", exportPropertySetter = false)
    var windowHeight = fixedHeight
        private set
    private var keyDownHook: ((Int, Int, Int) -> Unit)? = null

    /**
     * Sets the Lua function to be called when a keyboard key is pressed (key down).
     *
     * The Lua function will receive the following arguments:
     * 1. `keyCode` (Int): The unique ID of the key (GLFW key constant, e.g., 256 for ESC).
     * 2. `scanCode` (Int): The platform-specific hardware scancode of the key.
     * 3. `modifiers` (Int): A bitmask representing modifier keys held down (Shift, Ctrl, Alt, etc.).
     *
     * @param fn The Lua function to serve as the callback.
     */
    @LuaFunction(name = "set_key_down_hook")
    fun setKeyDownHook(fn: LuaFunc) {
        keyDownHook = fn::call
    }

    /**
     * Removes the currently assigned key down hook, disabling the callback.
     */
    @LuaFunction(name = "clear_key_down_hook")
    fun clearKeyDownHook() { keyDownHook = null }

    private var keyUpHook: ((Int, Int, Int) -> Unit)? = null

    /**
     * Sets the Lua function to be called when a keyboard key is released (key up).
     *
     * The Lua function will receive the following arguments:
     * 1. `keyCode` (Int): The unique ID of the key (GLFW key constant).
     * 2. `scanCode` (Int): The platform-specific hardware scancode of the key.
     * 3. `modifiers` (Int): A bitmask representing modifier keys held down (Shift, Ctrl, Alt, etc.).
     *
     * @param fn The Lua function to serve as the callback.
     */
    @LuaFunction(name = "set_key_up_hook")
    fun setKeyUpHook(fn: LuaFunc) {
        keyUpHook = fn::call
    }

    /**
     * Removes the currently assigned key up hook, disabling the callback.
     */
    @LuaFunction(name = "clear_key_up_hook")
    fun clearKeyUpHook() { keyUpHook = null }


    /* May dependent to Minecraft */

    /**
     * Opens this component as the current Minecraft screen (GUI).
     * Replaces the current screen.
     */
    @LuaFunction(name = "open")
    fun open() {
        if (attachedScreen != null)
            LogManager.getLogger().warn("Current screen already exists.")

        attachedScreen = object : Screen(Component.empty()), IKeyboardHandlerElement {
            override fun render(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
                if (minecraft?.level != null)
                    renderBackground(guiGraphics)
                else RenderUtil.renderer.loadMatrix(guiGraphics) {
                    RenderSystem.setShader(GameRenderer::getPositionColorShader)
                    fillRender(0, 0, width, height, 0, 0, 0, 0xff)
                }

                RenderUtil.renderer.loadMatrix(guiGraphics) {
                    this@ScreenRenderer.render(this, i.toDouble(), j.toDouble(), f)
                }

                super.render(guiGraphics, i, j, f)
            }

            override fun mouseClicked(i: Double, j: Double, button: Int) = onMouseDown(i, j, button)

            override fun mouseDragged(i: Double, j: Double, button: Int, f: Double, g: Double) =
                onMouseDrag(i, j, button)

            override fun mouseReleased(i: Double, j: Double, button: Int) = onMouseUp(i, j, button)

            override fun keyReleased(keyCode: Int, scanCode: Int, modifier: Int) =
                if(onKeyUp(keyCode, scanCode, modifier)) true
                else super.keyReleased(keyCode, scanCode, modifier)

            override fun keyPressed(keyCode: Int, scanCode: Int, modifier: Int) =
                if(onKeyDown(keyCode, scanCode, modifier)) true
                else super.keyPressed(keyCode, scanCode, modifier)

            override fun onClose() {
                super.onClose()
                attachedScreen = null
            }

            override fun init() {
                x = (width - height.toDouble() * fixedWidth / fixedHeight) / 2
                windowWidth = width.toDouble()
                windowHeight = height.toDouble()

                super.init()

                this@ScreenRenderer.width = height.toDouble() * fixedWidth / fixedHeight
                this@ScreenRenderer.height = height.toDouble()
            }

            override fun tick() {
                super.tick()
                this@ScreenRenderer.tick()
            }

            override fun shouldCloseOnEsc() = canExitWithEsc

            override fun keyDown(keyCode: Int, scanCode: Int, modifier: Int) = keyDownHook?.let {
                it.invoke(keyCode, scanCode, modifier)
                true
            } ?: false

            override fun keyUp(keyCode: Int, scanCode: Int, modifier: Int) = keyUpHook?.let {
                it.invoke(keyCode, scanCode, modifier)
                true
            } ?: false
        }
        Minecraft.getInstance().setScreen(attachedScreen as Screen)
    }

    /**
     * Closes the current screen if it is this component.
     */
    @LuaFunction(name = "close")
    fun close() {
        if (Minecraft.getInstance().screen == attachedScreen)
            Minecraft.getInstance().setScreen(null)
        else LogManager.getLogger().warn("Current screen is not same with attached screen")
    }
}