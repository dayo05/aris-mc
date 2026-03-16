package me.ddayo.aris.client.gui

import com.mojang.blaze3d.font.GlyphProvider
import com.mojang.blaze3d.platform.NativeImage
import me.ddayo.aris.RegistryHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.font.FontOption
import net.minecraft.client.gui.font.FontSet
import net.minecraft.client.gui.font.providers.TrueTypeGlyphProviderDefinition
import net.minecraft.client.renderer.texture.AbstractTexture
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackResources
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.ResourceMetadata
import org.apache.logging.log4j.LogManager
import java.io.ByteArrayInputStream
import java.io.File
import java.net.URL
import java.nio.ByteBuffer
import java.util.Optional
import java.util.function.Predicate
import java.util.stream.Stream


abstract class Resource(private val uri: String) {
    enum class ResourceType {
        Images,
        Videos,
        Fonts,
        Scripts,
        Sounds,
        Misc
    }

    abstract val resourceType: ResourceType

    private fun loadTester() {
        LogManager.getLogger().info(uri)
        if (uri.startsWith("jar:")) {
            finishLoad(
                javaClass.getResourceAsStream(
                    "assets/${resourceType.name.lowercase()}/${
                        uri.substring(
                            4
                        )
                    }"
                )!!.readBytes()
            )
            return
        }
        if (uri.startsWith("http:") || uri.startsWith("https:")) {
            finishLoad(URL(uri).readBytes())
            LogManager.getLogger().info("Network resource loaded!")
            return
        }
        val t = File("assets/${resourceType.name.lowercase()}", uri).let {
            if (it.exists())
                it.readBytes()
            else null
        }
        if (t != null) {
            finishLoad(t)
            return
        }
        throw IllegalArgumentException("Not able to load resource: $uri")
    }

    @Volatile
    lateinit var thrown: Throwable
        private set

    @Volatile
    protected lateinit var bytes: ByteArray
        private set

    fun await(): ByteArray {
        loader.join()
        if (::bytes.isInitialized) return bytes
        else throw thrown
    }

    protected open fun afterLoadAsync() {}
    protected open fun afterLoad() {}

    private val loader = Thread {
        try {
            loadTester()
            LogManager.getLogger().info("Resource $uri loaded.")
        } catch (e: Throwable) {
            thrown = e
            LogManager.getLogger().error(e.message)
            LogManager.getLogger().error(e.stackTraceToString())
        }
    }

    @Volatile
    var isLoaded = false
        private set

    @Volatile
    var cancelled = false
        private set

    fun cancel() {
        cancelled = true
    }

    private fun finishLoad(b: ByteArray) {
        bytes = b
        afterLoadAsync()
        Minecraft.getInstance().execute {
            if (!cancelled) {
                afterLoad()
                isLoaded = true
            }
        }
    }

    protected fun startLoading() {
        loader.start()
    }

    companion object {
        val renderableResources = mutableListOf<RenderableResource.RenderableResourceCompanion<*>>()
        fun clearResource() {
            renderableResources.forEach {
                it.clearResource()
            }
            FontResource.clearResource()
        }
    }
}

abstract class RenderableResource<T : RenderableResource<T>>(
    uri: String,
    val companion: RenderableResourceCompanion<T>
) : Resource(uri) {
    companion object {
        val DUMMY = RegistryHelper.getResourceLocation("dummy")
    }

    abstract class RenderableResourceCompanion<T : RenderableResource<T>>(private val of: String) {
        init {
            renderableResources.add(this)
        }
        var currentId = 0
            private set
        fun getNewIdentifier() = RegistryHelper.getResourceLocation("private/$of/${++currentId}")
        private val resourceMap = mutableMapOf<String, T>()
        fun getOrCreate(uri: String) = resourceMap.getOrPut(uri) { construct(uri) }
        abstract fun construct(uri: String): T

        fun clearResource() {
            val tm = Minecraft.getInstance().textureManager
            resourceMap.values.forEach {
                it.cancel()
                it.dispose()
                if (it.location != null && tm.getTexture(it.location) != null)
                    tm.release(it.location)
            }
            resourceMap.clear()
        }
    }

    protected var location: ResourceLocation? = null
        private set

    @Volatile
    var width: Int = -1
        protected set

    @Volatile
    var height: Int = -1
        protected set

    override fun afterLoad() {
        location = companion.getNewIdentifier()
        Minecraft.getInstance().textureManager.register(
            location, loadAsTexture()
        )
        location
    }

    protected open fun dispose() {}

    protected abstract fun loadAsTexture(): AbstractTexture

    fun bindTexture() {
        RenderUtil.renderer.currentTexture = location ?: DUMMY
    }
}

class ImageResource private constructor(uri: String) :
    RenderableResource<ImageResource>(uri, ImageResource) {
    companion object : RenderableResourceCompanion<ImageResource>("image") {
        override fun construct(uri: String) = ImageResource(uri)
    }

    override val resourceType: ResourceType
        get() = ResourceType.Images

    @Volatile
    private lateinit var ni: NativeImage

    override fun afterLoadAsync() {
        val dbuf = ByteBuffer.allocateDirect(bytes.size)
        dbuf.put(bytes)
        dbuf.flip()

        ni = NativeImage.read(dbuf)
        width = ni.width
        height = ni.height
    }

    override fun loadAsTexture(): AbstractTexture {
        return DynamicTexture(ni)
            .apply {
                setFilter(false, false)
            }
    }

    init {
        startLoading()
    }
}

class FontResource private constructor(uri: String, val size: Float, val oversample: Float, private val cid: Int) : Resource(uri) {
    companion object {
        private var cid = 0
        private val resourceMap = mutableMapOf<String, FontResource>()
        fun getOrCreate(uri: String, size: Float, oversample: Float) = resourceMap.getOrPut("$uri|$size|$oversample") { FontResource(uri, size, oversample, cid++) }

        fun clearResource() {
            resourceMap.values.forEach {
                it.cancel()
                it.set?.close()
            }
            resourceMap.clear()
        }
    }

    override val resourceType: ResourceType
        get() = ResourceType.Fonts

    @Volatile
    private var glyph: GlyphProvider? = null

    override fun afterLoadAsync() {
        val definition = TrueTypeGlyphProviderDefinition(
            ResourceLocation.withDefaultNamespace("dummy"),
            size,
            oversample,
            TrueTypeGlyphProviderDefinition.Shift(0f, 0f),
            ""
        )

        glyph = definition.unpack().left().get().load(object : ResourceManager {
            override fun getNamespaces() = emptySet<String>()
            override fun getResourceStack(resourceLocation: ResourceLocation?) =
                emptyList<net.minecraft.server.packs.resources.Resource>()

            override fun listResources(
                string: String?,
                predicate: Predicate<ResourceLocation?>?
            ) = emptyMap<ResourceLocation, net.minecraft.server.packs.resources.Resource>()

            override fun listResourceStacks(
                string: String?,
                predicate: Predicate<ResourceLocation?>?
            ) = emptyMap<ResourceLocation, List<net.minecraft.server.packs.resources.Resource>>()

            override fun listPacks() = Stream.empty<PackResources>()
            override fun getResource(resourceLocation: ResourceLocation?): Optional<net.minecraft.server.packs.resources.Resource> {
                return Optional.of(
                    net.minecraft.server.packs.resources.Resource(
                        null,
                        { ByteArrayInputStream(bytes) },
                        ResourceMetadata.EMPTY_SUPPLIER
                    )
                )
            }
        })
    }

    override fun afterLoad() {
        set = FontSet(Minecraft.getInstance().textureManager, RegistryHelper.getResourceLocation("font_$cid")).apply {
            reload(listOf(GlyphProvider.Conditional(glyph, FontOption.Filter.ALWAYS_PASS)), emptySet())
        }
        font = Font({ set }, false)
    }

    private var set: FontSet? = null
    var font: Font = Minecraft.getInstance().font
        private set

    init {
        startLoading()
    }
}