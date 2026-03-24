package me.ddayo.aris.client.gui

import com.mojang.blaze3d.vertex.*
import me.ddayo.aris.RegistryHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation
import org.joml.Quaternionf
import java.util.LinkedList
import kotlin.math.atan2
import kotlin.math.sqrt



class RenderUtil {
    private var currentTextureStack = LinkedList<ResourceLocation>()
    val currentTexture get() = currentTextureStack.first()

    lateinit var matrix: PoseStack
        private set
    lateinit var graphics: GuiGraphics
        private set

    fun loadMatrix(graphics: GuiGraphics, f: RenderUtil.() -> Unit) {
        this.matrix = graphics.pose()
        this.graphics = graphics
        this.f()
    }

    fun resourceExists(x: String): Boolean {
        return Minecraft.getInstance().resourceManager.getResource(
            RegistryHelper.getResourceLocation(
                "textures/$x"
            )
        ).isPresent
    }

    fun render(
        x: Double,
        y: Double,
        w: Double,
        h: Double,
        th1: Double,
        th2: Double,
        tv1: Double,
        tv2: Double
    ) = render(
        x.toFloat(),
        y.toFloat(),
        w.toFloat(),
        h.toFloat(),
        th1.toFloat(),
        th2.toFloat(),
        tv1.toFloat(),
        tv2.toFloat()
    )

    fun fillRender(
        x: Double,
        y: Double,
        w: Double,
        h: Double,
        r: Int,
        g: Int,
        b: Int,
        a: Int
    ) = fillRender(x.toFloat(), y.toFloat(), w.toFloat(), h.toFloat(), r, g, b, a)

    fun fillRender(x: Float, y: Float, w: Float, h: Float, r: Int, g: Int, b: Int, a: Int) {
        graphics.drawSpecial {
            it.getBuffer(RenderType.gui()).apply {
                quads(x, y, w, h, r, g, b, a)
            }
        }
    }

    fun fillRender(
        x: Double,
        y: Double,
        w: Double,
        h: Double,
        r: Float,
        g: Float,
        b: Float,
        a: Float
    ) = fillRender(x.toFloat(), y.toFloat(), w.toFloat(), h.toFloat(), r, g, b, a)

    fun fillRender(x: Float, y: Float, w: Float, h: Float, r: Float, g: Float, b: Float, a: Float) {
        graphics.drawSpecial {
            it.getBuffer(RenderType.gui()).apply {
                quadColor(x, y, w, h, r, g, b, a)
            }
        }
    }

    fun render(x: Float, y: Float, w: Float, h: Float, th1: Float, th2: Float, tv1: Float, tv2: Float) {
        graphics.drawSpecial {
            it.getBuffer(RenderType.guiTexturedOverlay(currentTexture))
                .apply {
                    quadColorTex(x, y, w, h, th1, th2, tv1, tv2, 1f, 1f, 1f, 1f)
                }
        }
    }

    private fun VertexConsumer.quadColorTex(
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        th1: Float,
        th2: Float,
        tv1: Float,
        tv2: Float,
        r: Float,
        g: Float,
        b: Float,
        a: Float
    ) {
        addVertex(matrix.last().pose(), x, y + h, 0.0f).setColor(r, g, b, a).setUv(th1, tv2)
        addVertex(matrix.last().pose(), x + w, y + h, 0.0f).setColor(r, g, b, a).setUv(th2, tv2)
        addVertex(matrix.last().pose(), x + w, y, 0.0f).setColor(r, g, b, a).setUv(th2, tv1)
        addVertex(matrix.last().pose(), x, y, 0.0f).setColor(r, g, b, a).setUv(th1, tv1)
    }

    private fun VertexConsumer.quads(x: Float, y: Float, w: Float, h: Float, r: Int, g: Int, b: Int, a: Int) {
        addVertex(matrix.last().pose(), x, y + h, 0.0f).setColor(r, g, b, a)
        addVertex(matrix.last().pose(), x + w, y + h, 0.0f).setColor(r, g, b, a)
        addVertex(matrix.last().pose(), x + w, y, 0.0f).setColor(r, g, b, a)
        addVertex(matrix.last().pose(), x, y, 0.0f).setColor(r, g, b, a)
    }

    private fun VertexConsumer.quadColor(
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        r: Float,
        g: Float,
        b: Float,
        a: Float
    ) {
        addVertex(matrix.last().pose(), x, y + h, 0.0f).setColor(r, g, b, a)
        addVertex(matrix.last().pose(), x + w, y + h, 0.0f).setColor(r, g, b, a)
        addVertex(matrix.last().pose(), x + w, y, 0.0f).setColor(r, g, b, a)
        addVertex(matrix.last().pose(), x, y, 0.0f).setColor(r, g, b, a)
    }

    fun push() = matrix.pushPose()
    fun pop() = matrix.popPose()

    fun translate(x: Double, y: Double, z: Double) = matrix.translate(x, y, z)

    fun rotate(x: Double, y: Double, z: Double) =
        matrix.mulPose(Quaternionf().rotationXYZ(x.toFloat(), y.toFloat(), z.toFloat()))

    fun scale(x: Double, y: Double, z: Double) = matrix.scale(x.toFloat(), y.toFloat(), z.toFloat())

    fun useTexture(x: ResourceLocation, f: () -> Unit) {
        currentTextureStack.push(x)
        f()
        currentTextureStack.pop()
    }

    fun useTexture(x: RenderableResource<*>, f: () -> Unit) {
        currentTextureStack.push(x.texture)
        f()
        currentTextureStack.pop()
    }

    fun render() = render(0, 0, 1920, 1080)

    fun fillRender(x: Int, y: Int, w: Int, h: Int, r: Int, g: Int, b: Int, a: Int) =
        fillRender(x.toDouble(), y.toDouble(), w.toDouble(), h.toDouble(), r, g, b, a)

    fun fillRender(x: Int, y: Int, w: Int, h: Int, r: Double, g: Double, b: Double, a: Double) = fillRender(
        x.toDouble(),
        y.toDouble(),
        w.toDouble(),
        h.toDouble(),
        r.toFloat(),
        g.toFloat(),
        b.toFloat(),
        a.toFloat()
    )

    fun fillRender(x: Double, y: Double, w: Double, h: Double, r: Double, g: Double, b: Double, a: Double) =
        fillRender(x, y, w, h, r.toFloat(), g.toFloat(), b.toFloat(), a.toFloat())

    fun render(x: Int, y: Int, w: Int, h: Int) = render(x.toDouble(), y.toDouble(), w.toDouble(), h.toDouble())

    fun render(x: Double, y: Double, w: Double, h: Double) = render(x, y, w, h, 0.0, 1.0, 0.0, 1.0)

    fun push(f: () -> Unit) {
        push()
        f()
        pop()
    }

    fun fixScale(width: Int, height: Int, newWidth: Int, newHeight: Int, x: () -> Unit) =
        fixScale(width.toDouble(), height.toDouble(), newWidth.toDouble(), newHeight.toDouble(), x)

    fun fixScale(width: Double, height: Double, newWidth: Double, newHeight: Double, x: () -> Unit) {
        push {
            translate((width - height * newWidth / newHeight) / 2, 0.0, 0.0)
            scale(height / newHeight, height / newHeight, height / newHeight)
            x()
        }
    }

    fun drawLine(sx: Double, sy: Double, ex: Double, ey: Double, d: Double, r: Int, g: Int, b: Int, a: Int = 255) {
        push {
            val dx = ex - sx
            val dy = ey - sy
            translate(sx, sy, 0.0)
            rotate(0.0, 0.0, Math.toDegrees(atan2(dy, dx)))
            fillRender(0.0, -d, sqrt(dx * dx + dy * dy), d * 2, r, g, b, a)
        }
    }

    companion object {
        val renderer get() = RenderUtil()
    }
}
