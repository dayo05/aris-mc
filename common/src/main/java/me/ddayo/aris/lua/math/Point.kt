package me.ddayo.aris.lua.math

import me.ddayo.aris.ILuaStaticDecl
import me.ddayo.aris.lua.glue.Point_LuaGenerated.pushLua
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import party.iroiro.luajava.Lua

@LuaProvider
data class Point(val x: Double, val y: Double): ILuaStaticDecl {
    constructor(x: Int, y: Int): this(x.toDouble(), y.toDouble())

    companion object {
        infix fun Double.with(other: Double) = Point(this, other)
        infix fun Int.with(other: Int) = Point(this, other)
    }

    @LuaFunction
    infix operator fun minus(other: Point) = (x - other.x) with (y - other.y)
    @LuaFunction
    infix operator fun plus(other: Point) = (x + other.x) with (y + other.y)
    @LuaFunction
    infix operator fun div(other: Double) = (x / other) with (y / other)
    infix operator fun div(other: Int) = (x / other) with (y / other)
    @LuaFunction
    infix fun center(other: Point) = (this + other) / 2

    @LuaFunction("into_string")
    fun intoString() = "($x, $y)"
    override fun toLua(lua: Lua) = pushLua(lua)
}