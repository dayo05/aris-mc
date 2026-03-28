package me.ddayo.aris.client.gui

import com.mojang.blaze3d.systems.RenderSystem
import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.engine.client.ClientInGameEngine
import me.ddayo.aris.engine.client.ClientMainEngine
import me.ddayo.aris.lua.glue.ClientInGameOnlyGenerated
import me.ddayo.aris.lua.glue.LuaClientOnlyGenerated
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import me.ddayo.aris.luagen.RetrieveEngine
import me.ddayo.aris.util.ListExtensions.mutableForEach
import net.minecraft.client.gui.GuiGraphics
import org.apache.logging.log4j.LogManager

@LuaProvider(ClientInGameEngine.PROVIDER)
class HudRenderer: BaseRectComponent(), ILuaStaticDecl by ClientInGameOnlyGenerated.HudRenderer_LuaGenerated {
    companion object {
        val enabledHud = mutableListOf<HudRenderer>()

        val hudRenderer = RenderUtil.renderer

        fun renderHud(graphics: GuiGraphics, delta: Float) {
            RenderSystem.enableBlend()
            hudRenderer.loadMatrix(graphics) {
                fixScale(graphics.guiWidth(), graphics.guiHeight(), 1920, 1080) {
                    enabledHud.mutableForEach {
                        it.render(this, 0.0, 0.0, delta)
                    }
                }
            }
        }

        init {
            InGameEngine.disposeHook.add { enabledHud.clear() }
        }
    }

    /**
     * Registers this renderer to the in-game HUD engine, making it visible on the HUD.
     */
    @LuaFunction(name = "open_hud")
    fun openHud() {
        enabledHud.add(this)
    }

    /**
     * Unregisters this renderer from the in-game HUD engine.
     */
    @LuaFunction(name = "close_hud")
    fun closeHud() {
        if(!enabledHud.remove(this))
            LogManager.getLogger().warn("Target hud not exists.")
    }
}