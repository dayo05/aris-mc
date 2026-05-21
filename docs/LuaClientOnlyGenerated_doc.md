## aris.client.create_area_builder() -> AreaBuilder
```
 Creates a new [AreaBuilder] instance to construct complex areas.
```
## aris.client.create_point(x: number, y: number) -> Point
```
 Creates a 2D [Point] object.
 @param x The x-coordinate.
 @param y The y-coordinate.
```
## aris.client.create_point(x: number, y: number, z: number) -> Point3
```
 Creates a 3D [Point3] object.
 @param x The x-coordinate.
 @param y The y-coordinate.
 @param z The z-coordinate.
```
## aris.client.create_window() -> ScreenRenderer
```
 Creates a new [ScreenRenderer] to manage a custom GUI screen.
```
## aris.client.create_image_renderer(res: ImageResource) -> ScriptImageRenderer
```
 Creates a component that renders a specific image resource.
 @param res The [ImageResource] loaded via `load_image`.
```
## aris.client.create_clickable(onClick: function, area: Area) -> ScriptMouseHandlerRenderer
```
 Creates an invisible clickable component defined by a custom [Area].
 @param onClick The Lua function to execute when clicked.
 @param area The definition of the clickable area.
```
## aris.client.create_clickable(onClick: function, x: number, y: number, width: number, height: number) -> ScriptMouseHandlerRenderer
```
 Creates an invisible clickable component defined by a rectangle.
 @param onClick The Lua function to execute when clicked.
 @param x The x-coordinate of the rectangle.
 @param y The y-coordinate of the rectangle.
 @param width The width of the rectangle.
 @param height The height of the rectangle.
```
## aris.client.create_color_renderer(r: number, g: number, b: number, a: number) -> ScriptColorRenderer
```
 Creates a solid color rectangle component.
 @param r Red component (0-255).
 @param g Green component (0-255).
 @param b Blue component (0-255).
 @param a Alpha component (0-255).
```
## aris.client.create_color_renderer(color: number) -> ScriptColorRenderer
```
 Creates a solid color rectangle component using a packed color value.
 @param color The ARGB color value.
```
## aris.client.create_default_text_renderer(text: string, color: number) -> ScriptDefaultTextRenderer
```
 Creates a text label component using the default Minecraft font.
 @param text The string to display.
 @param color The integer color of the text.
```
## aris.client.create_text_renderer(font: FontResource, text: string, color: number) -> ScriptTextRenderer
```
 Creates a text label component using the default Minecraft font.
 @param text The string to display.
 @param color The integer color of the text.
```
## aris.client.create_text_input_renderer(font: FontResource, text: string, color: number) -> ScriptTextInputRenderer
```
 Creates an editable single-line text input component.
 @param font The [FontResource] used to render the text.
 @param text The initial text content.
 @param color The integer color of the text.
```
## aris.client.create_checkbox_renderer(checked: boolean) -> ScriptCheckboxRenderer
```
 Creates a toggleable checkbox component.
 @param checked The initial checked state.
```
## aris.client.create_item_renderer(item: LuaItemStack) -> ScriptItemRenderer
```
 Creates a component that renders a Minecraft item stack.
 @param item The item stack to render.
```
## aris.client.load_image(path: string) -> ImageResource
```
 Loads an image resource from a file path.
 @param path The relative path to the image file.
 @return The loaded [ImageResource].
```
## aris.client.load_image(name: string, path: string) -> ImageResource
```
 Loads an image resource (alias overload).
 @param name Unused parameter (legacy or alias).
 @param path The relative path to the image file.
```
## aris.client.load_font(path: string, size: number) -> FontResource
```
 Loads a font resource
 @param path The relative path to the font file
 @param size Expected size to render
```
## aris.client.load_font(path: string, size: number, oversample: number) -> FontResource
```
 Loads a font resource
 @param path The relative path to the font file
 @param size Expected size to render
 @param oversample The depth of resolution sampling.(4.0f is recommended and this is the default)
```
## aris.client.create_component() -> BaseComponent
```
 Creates a basic, empty component container.
```
## aris.client.create_rect_component() -> BaseRectComponent
```
 Creates a basic component container with rectangular properties (width/height).
```
## aris.client.close_screen()
```
 Closes the current Minecraft screen (sets it to null).
```
## aris.client.get_keybinding(of: string) -> LuaKeyBinding
```
 Retrieves a Minecraft key binding by its internal name.
 @param of The name of the key binding to find.
 @return A [LuaKeyBinding] wrapper if found, or null.
```
## aris.game.client.hook.add_tick_hook(f: function)
```
 매 틱마다 실행할 함수를 추가합니다.
 @param f 실행할 함수
```
## aris.game.client.hook.clear_tick_hook(f: function)
```
 매 틱마다 실행할 함수를 초기화합니다.
```
## aris.game.client.hook.on_key_pressed(key: string, function: function)
```
 새로 추가한 조작키를 실행할때 실행될 함수를 지정합니다.
 @param key 누를 키
 @param function 실행할 함수
```
## aris.game.client.hook.on_player_leave_server(f: function)
```
 클라이언트가 서버에서 나갔을 때 실행할 함수를 추가합니다.

 엔진이 리로드되면 폐기되는 엔진에서 이 함수가 실행되므로,
 스크립트가 정리(cleanup) 작업을 수행할 수 있습니다.
 @param f 실행할 함수
```


## BaseComponent:add_child(child: BaseComponent)
```
 Adds a child component to this component.
 The child moves relative to this parent.

 @param child The component to add.
```


## BaseComponent:add_render_hook(fn: function)
```
 Adds a hook function to be invoked on each frame during rendering.

 @param fn The Lua function to call. (scaled_mouse_x, scaled_mouse_y, tick_delta) -> void
```


## BaseComponent:clear_render_hook()
```
 Clears all registered render hooks.
```


## BaseComponent:add_tick_hook(fn: function)


## BaseComponent:clear_tick_hook()


## BaseComponent:clear_child()
```
 Removes all child components from this component.
```


## BaseComponent:remove_child(child: BaseComponent)
```
 Removes a specific child component.

 @param child The component to remove.
```


## BaseComponent:get_local_coordinate(mx: number, my: number) -> LuaMultiReturn
```
 Recursively calculates the local mouse position by traversing up to the root parent.
 This converts global screen coordinates to coordinates relative to this component.
 @param mx Global X coordinate
 @param my Global Y coordinate
 @return local_x, local_y
```


## BaseComponent:get_global_coordinate(lx: number, ly: number) -> LuaMultiReturn
```
 Converts a local coordinate (relative to this component) to global screen coordinates.
 @param lx Local X coordinate
 @param ly Local Y coordinate
 @return global_x, global_y
```


## BaseComponent:set_x(new_value: number)
```
 The X coordinate of the component's position.
```


## BaseComponent:get_x() -> number
```
 The X coordinate of the component's position.
```


## BaseComponent:set_y(new_value: number)
```
 The Y coordinate of the component's position.
```


## BaseComponent:get_y() -> number
```
 The Y coordinate of the component's position.
```


## BaseComponent:set_rotation(new_value: number)
```
 The rotation of the component in degrees.
```


## BaseComponent:get_rotation() -> number
```
 The rotation of the component in degrees.
```


## BaseComponent:set_anchor_x(new_value: number)
```
 The X coordinate of the anchor point (pivot) relative to the component's origin.
 Rotation and scaling occur around this point.
```


## BaseComponent:get_anchor_x() -> number
```
 The X coordinate of the anchor point (pivot) relative to the component's origin.
 Rotation and scaling occur around this point.
```


## BaseComponent:set_anchor_y(new_value: number)
```
 The Y coordinate of the anchor point (pivot) relative to the component's origin.
 Rotation and scaling occur around this point.
```


## BaseComponent:get_anchor_y() -> number
```
 The Y coordinate of the anchor point (pivot) relative to the component's origin.
 Rotation and scaling occur around this point.
```


## BaseComponent:get_is_scale_rate_fixed() -> boolean
```
 Determines if the scale rate is locked.
 If true, [xScale] and [yScale] cannot be modified individually.
```


## BaseComponent:set_scale_x(new_value: number)
```
 The scale factor on the X axis.
 @throws AssertionError if [isScaleRateFixed] is true.
```


## BaseComponent:get_scale_x() -> number
```
 The scale factor on the X axis.
 @throws AssertionError if [isScaleRateFixed] is true.
```


## BaseComponent:set_scale_y(new_value: number)
```
 The scale factor on the Y axis.
 @throws AssertionError if [isScaleRateFixed] is true.
```


## BaseComponent:get_scale_y() -> number
```
 The scale factor on the Y axis.
 @throws AssertionError if [isScaleRateFixed] is true.
```


## BaseComponent:set_scale(new_value: number)
```
 A utility property to set or get uniform scaling for both X and Y axes.
 Getting this property asserts that [xScale] equals [yScale].
```


## BaseComponent:get_scale() -> number
```
 A utility property to set or get uniform scaling for both X and Y axes.
 Getting this property asserts that [xScale] equals [yScale].
```


## BaseComponent:set_is_visible(new_value: boolean)
```
 Controls whether the component is rendered.
```


## BaseComponent:get_is_visible() -> boolean
```
 Controls whether the component is rendered.
```


## BaseComponent:set_is_active(new_value: boolean)
```
 Controls whether the component accepts interactions (like clicks).
```


## BaseComponent:get_is_active() -> boolean
```
 Controls whether the component accepts interactions (like clicks).
```


## BaseComponent:get_parent() -> BaseComponent
```
 The parent component of this component, or null if it has no parent.
 This property cannot be set from Lua.
```


## LuaKeyBinding:consume() -> boolean


## LuaKeyBinding:get_is_down() -> boolean


## BaseRectComponent:set_width(new_value: number)
```
 The visual width of the component.
 Setting this updates [xScale] based on [fixedWidth].
```


## BaseRectComponent:get_width() -> number
```
 The visual width of the component.
 Setting this updates [xScale] based on [fixedWidth].
```


## BaseRectComponent:set_height(new_value: number)
```
 The visual height of the component.
 Setting this updates [yScale] based on [fixedHeight].
```


## BaseRectComponent:get_height() -> number
```
 The visual height of the component.
 Setting this updates [yScale] based on [fixedHeight].
```


## BaseRectComponent:set_fixed_width(new_value: number)
```
 The internal reference width (e.g., texture width).
 Changing this recalculates [xScale] to maintain visual [width].
```


## BaseRectComponent:get_fixed_width() -> number
```
 The internal reference width (e.g., texture width).
 Changing this recalculates [xScale] to maintain visual [width].
```


## BaseRectComponent:set_fixed_height(new_value: number)
```
 The internal reference height (e.g., texture height).
 Changing this recalculates [yScale] to maintain visual [height].
```


## BaseRectComponent:get_fixed_height() -> number
```
 The internal reference height (e.g., texture height).
 Changing this recalculates [yScale] to maintain visual [height].
```


## ScriptMouseHandlerRenderer:set_mouse_up_hook(fn: function)
```
 Sets the Lua function to be called when a mouse button is released (mouse up) within the component's area.

 The Lua function will receive the following arguments:
 1. `mx` (Double): The X coordinate of the mouse cursor.
 2. `my` (Double): The Y coordinate of the mouse cursor.
 3. `button` (Int): The index of the mouse button pressed.

 @param fn The Lua function to serve as the callback.
```


## ScriptMouseHandlerRenderer:clear_mouse_up_hook()
```
 Removes the currently assigned mouse up hook, disabling the callback.
```


## ScriptMouseHandlerRenderer:set_mouse_down_hook(fn: function)
```
 Sets the Lua function to be called when a mouse button is pressed (mouse down) within the component's area.

 The Lua function will receive the following arguments:
 1. `mx` (Double): The X coordinate of the mouse cursor.
 2. `my` (Double): The Y coordinate of the mouse cursor.
 3. `button` (Int): The index of the mouse button pressed.

 @param fn The Lua function to serve as the callback.
```


## ScriptMouseHandlerRenderer:clear_mouse_down_hook()
```
 Removes the currently assigned mouse down hook, disabling the callback.
```


## ScriptMouseHandlerRenderer:set_mouse_drag_hook(fn: function)
```
 Sets the Lua function to be called when the mouse is dragged within the component's area.

 The Lua function will receive the following arguments:
 1. `mx` (Double): The X coordinate of the mouse cursor.
 2. `my` (Double): The Y coordinate of the mouse cursor.
 3. `button` (Int): The index of the mouse button being held.

 @param fn The Lua function to serve as the callback.
```


## ScriptMouseHandlerRenderer:clear_mouse_drag_hook()
```
 Removes the currently assigned mouse drag hook, disabling the callback.
```


## ScriptMouseHandlerRenderer:set_area(new_value: Area)
```
 The area within which clicks are detected.
```


## ScriptMouseHandlerRenderer:get_area() -> Area
```
 The area within which clicks are detected.
```


## ScriptTextRenderer:set_font(new_value: FontResource)
```
 Font to use
```


## ScriptTextRenderer:get_font() -> FontResource
```
 Font to use
```


## ScriptTextRenderer:set_text(new_value: string)
```
 The text string to display.
```


## ScriptTextRenderer:get_text() -> string
```
 The text string to display.
```


## ScriptTextRenderer:set_color(new_value: number)
```
 The text color.
```


## ScriptTextRenderer:get_color() -> number
```
 The text color.
```


## ScriptDefaultTextRenderer:set_text(new_value: string)
```
 The text string to display.
```


## ScriptDefaultTextRenderer:get_text() -> string
```
 The text string to display.
```


## ScriptDefaultTextRenderer:set_color(new_value: number)
```
 The text color.
```


## ScriptDefaultTextRenderer:get_color() -> number
```
 The text color.
```


## ScriptTextInputRenderer:set_focused(focused: boolean)
```
 Sets the focus state programmatically.
```


## ScriptTextInputRenderer:get_selected_text() -> string
```
 Returns the currently selected substring (empty if there is no selection).
```


## ScriptTextInputRenderer:set_change_hook(fn: function)
```
 Sets the Lua function called whenever the text content changes.
 The callback receives the new text (String).
```


## ScriptTextInputRenderer:clear_change_hook()
```
 Removes the currently assigned change hook.
```


## ScriptTextInputRenderer:set_enter_hook(fn: function)
```
 Sets the Lua function called when Enter is pressed while focused.
 The callback receives the current text (String).
```


## ScriptTextInputRenderer:clear_enter_hook()
```
 Removes the currently assigned enter hook.
```


## ScriptTextInputRenderer:set_focus_hook(fn: function)
```
 Sets the Lua function called when focus is gained or lost.
 The callback receives the new focus state (Boolean).
```


## ScriptTextInputRenderer:clear_focus_hook()
```
 Removes the currently assigned focus hook.
```


## ScriptTextInputRenderer:set_font(new_value: FontResource)
```
 Font to use for rendering the text.
```


## ScriptTextInputRenderer:get_font() -> FontResource
```
 Font to use for rendering the text.
```


## ScriptTextInputRenderer:set_color(new_value: number)
```
 The color of the text (RGB, optionally ARGB).
```


## ScriptTextInputRenderer:get_color() -> number
```
 The color of the text (RGB, optionally ARGB).
```


## ScriptTextInputRenderer:set_text(new_value: string)
```
 The current text content. Setting this from Lua clamps the cursor/selection.
```


## ScriptTextInputRenderer:get_text() -> string
```
 The current text content. Setting this from Lua clamps the cursor/selection.
```


## ScriptTextInputRenderer:set_width(new_value: number)
```
 The width of the input box.
```


## ScriptTextInputRenderer:get_width() -> number
```
 The width of the input box.
```


## ScriptTextInputRenderer:set_height(new_value: number)
```
 The height of the input box.
```


## ScriptTextInputRenderer:get_height() -> number
```
 The height of the input box.
```


## ScriptTextInputRenderer:set_padding(new_value: number)
```
 Horizontal/vertical inner padding between the box border and the text.
```


## ScriptTextInputRenderer:get_padding() -> number
```
 Horizontal/vertical inner padding between the box border and the text.
```


## ScriptTextInputRenderer:set_border_width(new_value: number)
```
 Thickness of the box border. Set to 0 to disable.
```


## ScriptTextInputRenderer:get_border_width() -> number
```
 Thickness of the box border. Set to 0 to disable.
```


## ScriptTextInputRenderer:set_background_color(new_value: number)
```
 Background fill color (ARGB). Alpha 0 disables the background.
```


## ScriptTextInputRenderer:get_background_color() -> number
```
 Background fill color (ARGB). Alpha 0 disables the background.
```


## ScriptTextInputRenderer:set_border_color(new_value: number)
```
 Border color (ARGB) used when the field is not focused.
```


## ScriptTextInputRenderer:get_border_color() -> number
```
 Border color (ARGB) used when the field is not focused.
```


## ScriptTextInputRenderer:set_focused_border_color(new_value: number)
```
 Border color (ARGB) used when the field is focused.
```


## ScriptTextInputRenderer:get_focused_border_color() -> number
```
 Border color (ARGB) used when the field is focused.
```


## ScriptTextInputRenderer:set_selection_color(new_value: number)
```
 Color (ARGB) of the text selection highlight.
```


## ScriptTextInputRenderer:get_selection_color() -> number
```
 Color (ARGB) of the text selection highlight.
```


## ScriptTextInputRenderer:set_placeholder(new_value: string)
```
 Placeholder text shown while the field is empty.
```


## ScriptTextInputRenderer:get_placeholder() -> string
```
 Placeholder text shown while the field is empty.
```


## ScriptTextInputRenderer:set_placeholder_color(new_value: number)
```
 Color of the placeholder text.
```


## ScriptTextInputRenderer:get_placeholder_color() -> number
```
 Color of the placeholder text.
```


## ScriptTextInputRenderer:set_max_length(new_value: number)
```
 The maximum number of characters allowed.
```


## ScriptTextInputRenderer:get_max_length() -> number
```
 The maximum number of characters allowed.
```


## ScriptTextInputRenderer:set_text_scale(new_value: number)
```
 Scale factor applied to the rendered text (the font is otherwise drawn at its native ~9px size).
```


## ScriptTextInputRenderer:get_text_scale() -> number
```
 Scale factor applied to the rendered text (the font is otherwise drawn at its native ~9px size).
```


## ScriptTextInputRenderer:set_is_editable(new_value: boolean)
```
 Whether the field is editable. When false, the field can still be focused but ignores edits.
```


## ScriptTextInputRenderer:get_is_editable() -> boolean
```
 Whether the field is editable. When false, the field can still be focused but ignores edits.
```


## ScriptTextInputRenderer:get_is_focused() -> boolean
```
 Whether the field currently has keyboard focus. Read-only in Lua; use `set_focused`.
```


## ScriptItemRenderer:set_item(new_value: LuaItemStack)
```
 The Minecraft item to render, wrapped for Lua access.
```


## ScriptItemRenderer:get_item() -> LuaItemStack
```
 The Minecraft item to render, wrapped for Lua access.
```


## ScriptItemRenderer:set_display_tooltip(new_value: boolean)
```
 Whether to render the item's tooltip when the cursor hovers over it.
```


## ScriptItemRenderer:get_display_tooltip() -> boolean
```
 Whether to render the item's tooltip when the cursor hovers over it.
```


## ScriptCheckboxRenderer:set_change_hook(fn: function)
```
 Sets the Lua function called when the checked state is toggled by a click.
 The callback receives the new checked state (Boolean).
```


## ScriptCheckboxRenderer:clear_change_hook()
```
 Removes the currently assigned change hook.
```


## ScriptCheckboxRenderer:set_size(new_value: number)
```
 The side length of the checkbox box.
```


## ScriptCheckboxRenderer:get_size() -> number
```
 The side length of the checkbox box.
```


## ScriptCheckboxRenderer:set_border_width(new_value: number)
```
 Thickness of the box border. Set to 0 to disable.
```


## ScriptCheckboxRenderer:get_border_width() -> number
```
 Thickness of the box border. Set to 0 to disable.
```


## ScriptCheckboxRenderer:set_background_color(new_value: number)
```
 Background fill color (ARGB). Alpha 0 disables the background.
```


## ScriptCheckboxRenderer:get_background_color() -> number
```
 Background fill color (ARGB). Alpha 0 disables the background.
```


## ScriptCheckboxRenderer:set_border_color(new_value: number)
```
 Border color (ARGB).
```


## ScriptCheckboxRenderer:get_border_color() -> number
```
 Border color (ARGB).
```


## ScriptCheckboxRenderer:set_check_color(new_value: number)
```
 Color (ARGB) of the check mark drawn when checked.
```


## ScriptCheckboxRenderer:get_check_color() -> number
```
 Color (ARGB) of the check mark drawn when checked.
```


## ScriptCheckboxRenderer:set_is_checked(new_value: boolean)
```
 Whether the checkbox is currently checked.
```


## ScriptCheckboxRenderer:get_is_checked() -> boolean
```
 Whether the checkbox is currently checked.
```


## ScreenRenderer:set_key_down_hook(fn: function)
```
 Sets the Lua function to be called when a keyboard key is pressed (key down).

 The Lua function will receive the following arguments:
 1. `keyCode` (Int): The unique ID of the key (GLFW key constant, e.g., 256 for ESC).
 2. `scanCode` (Int): The platform-specific hardware scancode of the key.
 3. `modifiers` (Int): A bitmask representing modifier keys held down (Shift, Ctrl, Alt, etc.).

 @param fn The Lua function to serve as the callback.
```


## ScreenRenderer:clear_key_down_hook()
```
 Removes the currently assigned key down hook, disabling the callback.
```


## ScreenRenderer:set_key_up_hook(fn: function)
```
 Sets the Lua function to be called when a keyboard key is released (key up).

 The Lua function will receive the following arguments:
 1. `keyCode` (Int): The unique ID of the key (GLFW key constant).
 2. `scanCode` (Int): The platform-specific hardware scancode of the key.
 3. `modifiers` (Int): A bitmask representing modifier keys held down (Shift, Ctrl, Alt, etc.).

 @param fn The Lua function to serve as the callback.
```


## ScreenRenderer:clear_key_up_hook()
```
 Removes the currently assigned key up hook, disabling the callback.
```


## ScreenRenderer:set_close_hook(fn: function)
```
 Sets the Lua function to be called when the screen is closed.

 The Lua function receives no arguments.

 @param fn The Lua function to serve as the callback.
```


## ScreenRenderer:clear_close_hook()
```
 Removes the currently assigned close hook, disabling the callback.
```


## ScreenRenderer:open()
```
 Opens this component as the current Minecraft screen (GUI).
 Replaces the current screen.
```


## ScreenRenderer:close()
```
 Closes the current screen if it is this component.
```


## ScreenRenderer:set_can_exit_with_esc(new_value: boolean)
```
 Determines if the screen can be closed by pressing the Escape key.
```


## ScreenRenderer:get_can_exit_with_esc() -> boolean
```
 Determines if the screen can be closed by pressing the Escape key.
```


## ScreenRenderer:get_window_width() -> number
```
 The actual width of the game window.
 This property is read-only in Lua.
```


## ScreenRenderer:get_window_height() -> number
```
 The actual height of the game window.
 This property is read-only in Lua.
```


## ScriptImageRenderer:get_uv1() -> LuaMultiReturn
```
 Returns the UV coordinates of the first corner.
 @return MultiReturn(u, v)
```


## ScriptImageRenderer:get_uv2() -> LuaMultiReturn
```
 Returns the UV coordinates of the second corner.
 @return MultiReturn(u, v)
```


## ScriptImageRenderer:set_uv1(u: number, v: number)
```
 Sets the UV coordinates for the first corner.
```


## ScriptImageRenderer:set_uv2(u: number, v: number)
```
 Sets the UV coordinates for the second corner.
```


## ScriptImageRenderer:crop_uv1(u: number, v: number)
```
 Crops the image UV from the top-left (UV1) side, adjusting position and size to match visual changes.
```


## ScriptImageRenderer:crop_uv2(u: number, v: number)
```
 Crops the image UV from the bottom-right (UV2) side, adjusting size to match.
```


## ScriptImageRenderer:set_image(new_value: ImageResource)
```
 The image resource to be rendered.
```


## ScriptImageRenderer:get_image() -> ImageResource
```
 The image resource to be rendered.
```


## ScriptColorRenderer:set_r(new_value: number)
```
 The red component of the color (0-255).
```


## ScriptColorRenderer:get_r() -> number
```
 The red component of the color (0-255).
```


## ScriptColorRenderer:set_g(new_value: number)
```
 The green component of the color (0-255).
```


## ScriptColorRenderer:get_g() -> number
```
 The green component of the color (0-255).
```


## ScriptColorRenderer:set_b(new_value: number)
```
 The blue component of the color (0-255).
```


## ScriptColorRenderer:get_b() -> number
```
 The blue component of the color (0-255).
```


## ScriptColorRenderer:set_a(new_value: number)
```
 The alpha component of the color (0-255).
```


## ScriptColorRenderer:get_a() -> number
```
 The alpha component of the color (0-255).
```


## ScriptColorRenderer:set_color(new_value: number)
```
 The full color value packed into a Long (ARGB format).
 Setting this updates [r], [g], [b], and [a].
```


## ScriptColorRenderer:get_color() -> number
```
 The full color value packed into a Long (ARGB format).
 Setting this updates [r], [g], [b], and [a].
```
