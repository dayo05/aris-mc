package me.ddayo.aris.client.gui.element

import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.engine.client.ClientMainEngine
import me.ddayo.aris.client.gui.BaseComponent
import me.ddayo.aris.client.gui.RenderUtil
import me.ddayo.aris.lua.glue.LuaClientOnlyGenerated
import me.ddayo.aris.luagen.LuaProperty
import me.ddayo.aris.luagen.LuaProvider
import net.minecraft.client.gui.Font

@LuaProvider(ClientMainEngine.PROVIDER)
class ScriptDefaultTextRenderer(
    /**
     * The text string to display.
     */
    @LuaProperty
    var text: String,
    private val font: Font,
    /**
     * The text color.
     */
    @LuaProperty
    var color: Int
) : BaseComponent(),
    ILuaStaticDecl by LuaClientOnlyGenerated.ScriptDefaultTextRenderer_LuaGenerated {
    override val isScaleRateFixed = true

    override fun RenderUtil._render(mx: Double, my: Double, delta: Float) {
        graphics.drawString(
            font,
            text,
            0,
            0,
            color
        )
    }
}