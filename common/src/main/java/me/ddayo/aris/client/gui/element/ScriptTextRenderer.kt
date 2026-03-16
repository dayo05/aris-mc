package me.ddayo.aris.client.gui.element

import me.ddayo.aris.client.gui.BaseComponent
import me.ddayo.aris.client.gui.FontResource
import me.ddayo.aris.client.gui.RenderUtil
import me.ddayo.aris.engine.client.ClientMainEngine
import me.ddayo.aris.lua.glue.LuaClientOnlyGenerated
import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.luagen.LuaProperty
import me.ddayo.aris.luagen.LuaProvider
import net.minecraft.client.Minecraft

@LuaProvider(ClientMainEngine.PROVIDER)
class ScriptTextRenderer(
    /**
     * Font to use
     */
    @LuaProperty
    var font: FontResource,
    /**
     * The text string to display.
     */
    @LuaProperty
    var text: String,
    /**
     * The text color.
     */
    @LuaProperty
    var color: Int
): BaseComponent(), ILuaStaticDecl by LuaClientOnlyGenerated.ScriptTextRenderer_LuaGenerated {
    override val isScaleRateFixed = true

    override fun RenderUtil._render(mx: Double, my: Double, delta: Float) {
        graphics.drawString(
            font.font,
            text,
            0,
            0,
            color
        )
    }
}

@LuaProvider(ClientMainEngine.PROVIDER)
class ScriptDefaultTextRenderer(
    /**
     * The text string to display.
     */
    @LuaProperty
    var text: String,
    /**
     * The text color.
     */
    @LuaProperty
    var color: Int
) : BaseComponent(),
    ILuaStaticDecl by LuaClientOnlyGenerated.ScriptDefaultTextRenderer_LuaGenerated {
    override val isScaleRateFixed = true

    val font = Minecraft.getInstance().font
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