package me.ddayo.aris.client.gui.element

import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.engine.client.ClientMainEngine
import me.ddayo.aris.client.gui.BaseComponent
import me.ddayo.aris.client.gui.RenderUtil
import me.ddayo.aris.engine.wrapper.LuaItemStack
import me.ddayo.aris.lua.glue.LuaClientOnlyGenerated
import me.ddayo.aris.luagen.LuaProperty
import me.ddayo.aris.luagen.LuaProvider
import net.minecraft.client.Minecraft
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

@LuaProvider(ClientMainEngine.PROVIDER)
class ScriptItemRenderer(
    private var item: ItemStack
) : BaseComponent(),
    ILuaStaticDecl by LuaClientOnlyGenerated.ScriptItemRenderer_LuaGenerated {
    companion object {
        private const val ITEM_SIZE = 16
    }

    override val isScaleRateFixed = true

    /**
     * The Minecraft item to render, wrapped for Lua access.
     */
    @LuaProperty("item")
    var luaItem
        get() = LuaItemStack(item)
        set(value) {
            item = value.inner
        }

    /**
     * Whether to render the item's tooltip when the cursor hovers over it.
     */
    @LuaProperty("display_tooltip")
    var displayTooltip = false

    override fun RenderUtil._render(mx: Double, my: Double, delta: Float) {
        val stack = if (item.isEmpty) Items.BARRIER.defaultInstance else item
        this.graphics.renderItem(stack, 0, 0)
        if (displayTooltip && !item.isEmpty &&
            mx in 0.0..ITEM_SIZE.toDouble() && my in 0.0..ITEM_SIZE.toDouble()
        ) {
            val mc = Minecraft.getInstance()
            val window = mc.window
            // Cursor position in GUI-scaled space (independent of this component's transform).
            val mouseX = (mc.mouseHandler.xpos() * window.guiScaledWidth / window.screenWidth).toInt()
            val mouseY = (mc.mouseHandler.ypos() * window.guiScaledHeight / window.screenHeight).toInt()
            // Render the tooltip in screen space by resetting the accumulated pose, so it is not
            // scaled or offset by this component's (and its parents') matrix.
            matrix.pushPose()
            matrix.last().pose().identity()
            this.graphics.renderTooltip(mc.font, item, mouseX, mouseY)
            matrix.popPose()
        }
    }
}