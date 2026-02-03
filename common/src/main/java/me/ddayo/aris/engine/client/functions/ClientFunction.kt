package me.ddayo.aris.engine.client.functions

import me.ddayo.aris.luagen.LuaFunc
import me.ddayo.aris.client.gui.BaseComponent
import me.ddayo.aris.client.gui.BaseRectComponent
import me.ddayo.aris.client.gui.ImageResource
import me.ddayo.aris.math.Area
import me.ddayo.aris.math.AreaBuilder
import me.ddayo.aris.math.Point
import me.ddayo.aris.math.Point.Companion.with
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import me.ddayo.aris.client.gui.ScreenRenderer
import me.ddayo.aris.client.gui.element.*
import me.ddayo.aris.engine.wrapper.LuaItemStack
import me.ddayo.aris.engine.client.ClientMainEngine
import me.ddayo.aris.engine.client.wrapper.LuaKeyBinding
import me.ddayo.aris.math.Point3
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.Minecraft

@LuaProvider(ClientMainEngine.PROVIDER, library = "aris.client")
@Environment(EnvType.CLIENT)
object ClientFunction {
    /**
     * Creates a new [AreaBuilder] instance to construct complex areas.
     */
    @LuaFunction(name = "create_area_builder")
    fun create() = AreaBuilder()

    /**
     * Creates a 2D [Point] object.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     */
    @LuaFunction("create_point")
    fun create(x: Double, y: Double) = Point(x, y)

    /**
     * Creates a 3D [Point3] object.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @param z The z-coordinate.
     */
    @LuaFunction("create_point")
    fun create(x: Double, y: Double, z: Double) = Point3(x, y, z)

    /**
     * Creates a new [ScreenRenderer] to manage a custom GUI screen.
     */
    @LuaFunction("create_window")
    fun createWindow() = ScreenRenderer()

    /**
     * Creates a component that renders a specific image resource.
     * @param res The [ImageResource] loaded via `load_image`.
     */
    @LuaFunction("create_image_renderer")
    fun createImageRenderer(res: ImageResource) = ScriptImageRenderer(res)

    /**
     * Creates an invisible clickable component defined by a custom [Area].
     * @param onClick The Lua function to execute when clicked.
     * @param area The definition of the clickable area.
     */
    @LuaFunction("create_clickable")
    fun createClickable(onClick: LuaFunc, area: Area) =
        ScriptMouseHandlerRenderer(onClick::call, area)

    /**
     * Creates an invisible clickable component defined by a rectangle.
     * @param onClick The Lua function to execute when clicked.
     * @param x The x-coordinate of the rectangle.
     * @param y The y-coordinate of the rectangle.
     * @param width The width of the rectangle.
     * @param height The height of the rectangle.
     */
    @LuaFunction("create_clickable")
    fun createClickable(onClick: LuaFunc, x: Int, y: Int, width: Int, height: Int) =
        ScriptMouseHandlerRenderer(
            onClick::call,
            Area(x with y, x with (y + height), (x + width) with (y + height), (x + width) with y)
        )

    /**
     * Creates a solid color rectangle component.
     * @param r Red component (0-255).
     * @param g Green component (0-255).
     * @param b Blue component (0-255).
     * @param a Alpha component (0-255).
     */
    @LuaFunction("create_color_renderer")
    fun createColorRenderer(r: Int, g: Int, b: Int, a: Int) = ScriptColorRenderer(r, g, b, a)

    /**
     * Creates a solid color rectangle component using a packed color value.
     * @param color The ARGB color value.
     */
    @LuaFunction("create_color_renderer")
    fun createColorRenderer(color: Long) = ScriptColorRenderer(color)

    /**
     * Creates a text label component using the default Minecraft font.
     * @param text The string to display.
     * @param color The integer color of the text.
     */
    @LuaFunction("create_default_text_renderer")
    fun createDefaultTextRenderer(text: String, color: Int) =
        ScriptDefaultTextRenderer(text, Minecraft.getInstance().font, color)

    /**
     * Creates a component that renders a Minecraft item stack.
     * @param item The item stack to render.
     */
    @LuaFunction("create_item_renderer")
    fun createItemRenderer(item: LuaItemStack) = ScriptItemRenderer(item.inner)

    /**
     * Loads an image resource from a file path.
     * @param path The relative path to the image file.
     * @return The loaded [ImageResource].
     */
    @LuaFunction("load_image")
    fun loadImageRuntime(path: String): ImageResource = ImageResource.getOrCreate(path)

    /**
     * Loads an image resource (alias overload).
     * @param name Unused parameter (legacy or alias).
     * @param path The relative path to the image file.
     */
    @LuaFunction("load_image")
    fun loadImageRuntime(name: String, path: String): ImageResource {
        return ImageResource.getOrCreate(path)
    }

    /**
     * Creates a basic, empty component container.
     */
    @LuaFunction("create_component")
    fun createComponent() = BaseComponent()

    /**
     * Creates a basic component container with rectangular properties (width/height).
     */
    @LuaFunction("create_rect_component")
    fun createRectComponent() = BaseRectComponent()

    /**
     * Closes the current Minecraft screen (sets it to null).
     */
    @LuaFunction("close_screen")
    fun closeScreen() = Minecraft.getInstance().setScreen(null)

    /**
     * Retrieves a Minecraft key binding by its internal name.
     * @param of The name of the key binding to find.
     * @return A [LuaKeyBinding] wrapper if found, or null.
     */
    @LuaFunction("get_keybinding")
    fun getKeybinding(of: String) =
        Minecraft.getInstance().options.keyMappings.find { it.name == of }?.let { LuaKeyBinding(it) }
}