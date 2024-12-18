package me.ddayo.aris.client.gui

import me.ddayo.aris.ILuaStaticDecl
import me.ddayo.aris.LuaFunc
import me.ddayo.aris.client.gui.element.BaseWidget
import me.ddayo.aris.lua.glue.LuaGenerated
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import org.apache.logging.log4j.LogManager

@LuaProvider
class BaseScreen : Screen(Component.empty()), ILuaStaticDecl by LuaGenerated.BaseScreen_LuaGenerated {
    override fun render(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        if(minecraft?.level != null)
            renderBackground(guiGraphics)
        else guiGraphics.fill(0, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight(), 0xff000000.toInt())

        renderHooks.indices.forEach {
            renderHooks[it].call(i, j, f)
        }

        super.render(guiGraphics, i, j, f)
    }

    @LuaFunction(name = "open")
    fun openScreen() {
        Minecraft.getInstance().setScreen(this)
        LogManager.getLogger().info("Open")
    }

    override fun init() {
        addedWidgets.forEach {
            addRenderableWidget(it)
        }
        super.init()
    }

    private val addedWidgets = mutableListOf<AbstractWidget>()

    @LuaFunction(name = "add_child")
    fun addChild(child: BaseWidget) {
        addedWidgets.add(child)
        rebuildWidgets()
    }

    private val renderHooks = mutableListOf<LuaFunc>()
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
        addedWidgets.clear()
        rebuildWidgets()
    }

    override fun onClose() {
        super.onClose()

        LogManager.getLogger().info("Close")
    }
}