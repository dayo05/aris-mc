package me.ddayo.aris.math

import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.lua.glue.LuaGenerated
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProperty
import me.ddayo.aris.luagen.LuaProvider
import me.ddayo.aris.math.Point
import me.ddayo.aris.math.Point.Companion.with


@LuaProvider(library = "aris.math")
object PointFunctions {
    /**
     * Create point object (x, y)
     * @return Point(x, y)
     */
    @LuaFunction("create_point")
    fun createPoint(x: Double, y: Double) = x with y

    /**
     * Create point object (x, y, z)
     * @return Point3(x, y, z)
     */
    @LuaFunction("create_point")
    fun createPoint(x: Double, y: Double, z: Double) = Point3(x, y, z)
}

@LuaProvider
data class Point3(@LuaProperty var x: Double, @LuaProperty var y: Double, @LuaProperty var z: Double): ILuaStaticDecl by LuaGenerated.Point3_LuaGenerated {
    constructor(x: Int, y: Int, z: Int): this(x.toDouble(), y.toDouble(), z.toDouble())

    @LuaFunction
    infix operator fun minus(other: Point3) = (x - other.x) with (y - other.y)
    @LuaFunction
    infix operator fun plus(other: Point3) = (x + other.x) with (y + other.y)
    @LuaFunction
    infix operator fun div(other: Double) = (x / other) with (y / other)
    infix operator fun div(other: Int) = (x / other) with (y / other)
    @LuaFunction
    infix fun center(other: Point3) = (this + other) / 2

    @LuaFunction("into_string")
    fun intoString() = "($x, $y, $z)"
}

@LuaProvider
data class Point(@LuaProperty var x: Double, @LuaProperty var y: Double): ILuaStaticDecl by LuaGenerated.Point_LuaGenerated {
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
}