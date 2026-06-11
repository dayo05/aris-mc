@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package me.ddayo.aris.client.sound

import net.minecraft.client.Minecraft
import net.minecraft.client.resources.sounds.Sound
import net.minecraft.client.sounds.WeighedSoundEvents
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.Resource
import net.minecraft.server.packs.resources.ResourceMetadata
import net.minecraft.util.valueproviders.ConstantFloat
import me.ddayo.aris.mixin.SoundManagerAccessor
import java.io.ByteArrayInputStream
import java.io.File

object ClientSoundRegistry {
    private val soundEvents = mutableSetOf<ResourceLocation>()
    private val customSounds = mutableMapOf<ResourceLocation, CustomSound>()

    fun register(id: String) {
        soundEvents += ResourceLocation.parse(id)
    }

    fun registerRaw(id: String, path: String) = registerRaw(id, path, false)

    fun registerRaw(id: String, path: String, stream: Boolean) {
        require(path.endsWith(".ogg")) { "Only .ogg raw sounds are supported by Minecraft's sound engine: $path" }

        val file = File("assets/sounds", path)
        require(file.exists()) { "Raw sound file does not exist: ${file.path}" }
        require(file.isFile) { "Raw sound path is not a file: ${file.path}" }

        val location = ResourceLocation.parse(id)
        customSounds[location] = CustomSound(file.readBytes(), stream)
        installRaw(location, reload = true)
    }

    fun installRaw(id: String) {
        installRaw(ResourceLocation.parse(id), reload = false)
    }

    fun hasRaw(id: String) = ResourceLocation.parse(id) in customSounds

    private fun installRaw(location: ResourceLocation, reload: Boolean) {
        val customSound = customSounds[location] ?: return
        val manager = Minecraft.getInstance().soundManager
        val accessor = manager as SoundManagerAccessor
        val sound = Sound(
            location,
            ConstantFloat.of(1.0f),
            ConstantFloat.of(1.0f),
            1,
            Sound.Type.FILE,
            customSound.stream,
            false,
            16
        )

        accessor.`aris$getRegistry`()[location] = WeighedSoundEvents(location, null).apply {
            addSound(sound)
        }
        accessor.`aris$getSoundCache`()[sound.getPath()] = Resource(
            null,
            { ByteArrayInputStream(customSound.bytes) },
            ResourceMetadata.EMPTY_SUPPLIER
        )
        if (reload) {
            manager.reload()
        }
    }

    private fun soundPath(location: ResourceLocation) =
        ResourceLocation.fromNamespaceAndPath(location.namespace, "sounds/${location.path}.ogg")

    private data class CustomSound(
        val bytes: ByteArray,
        val stream: Boolean,
    )
}
