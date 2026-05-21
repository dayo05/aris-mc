package me.ddayo.aris.client.gui.element

interface IKeyboardHandlerElement {
    fun keyDown(keyCode: Int, scanCode: Int, modifier: Int): Boolean
    fun keyUp(keyCode: Int, scanCode: Int, modifier: Int): Boolean

    /**
     * Called when a printable character is typed (after IME/keyboard composition).
     * @param codePoint The typed character.
     * @param modifier Bitmask of modifier keys held down.
     * @return true if the event was consumed.
     */
    fun charTyped(codePoint: Char, modifier: Int): Boolean = false
}
