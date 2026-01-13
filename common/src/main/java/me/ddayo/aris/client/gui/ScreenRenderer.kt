package me.ddayo.aris.client.gui

import com.mojang.blaze3d.systems.RenderSystem
import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.engine.client.ClientMainEngine
import me.ddayo.aris.lua.glue.LuaClientOnlyGenerated
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

    /* May dependent to Minecraft */

    /**
     * Opens this component as the current Minecraft screen (GUI).
     * Replaces the current screen.
     */
    @LuaFunction(name = "open")
    fun open() {
        if (attachedScreen != null)
            LogManager.getLogger().warn("Current screen already exists.")

        attachedScreen = object : Screen(Component.empty()) {
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

            override fun mouseReleased(i: Double, j: Double, button: Int) = onMouseRelease(i, j, button)

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