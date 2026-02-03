package me.ddayo.aris.client.gui.element

interface IMouseHandlerElement {
    fun mouseDown(mx: Double, my: Double, button: Int): Boolean { return false }
    fun mouseDrag(mx: Double, my: Double, button: Int): Boolean { return false }
    fun mouseUp(mx: Double, my: Double, button: Int): Boolean
}