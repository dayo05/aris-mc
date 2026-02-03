package me.ddayo.aris.client.gui.element

interface IKeyboardHandlerElement {
    fun keyDown(keyCode: Int, scanCode: Int, modifier: Int): Boolean
    fun keyUp(keyCode: Int, scanCode: Int, modifier: Int): Boolean
}