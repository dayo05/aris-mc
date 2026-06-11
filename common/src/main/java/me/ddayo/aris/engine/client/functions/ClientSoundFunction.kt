package me.ddayo.aris.engine.client.functions

import me.ddayo.aris.client.sound.ClientSoundRegistry
import me.ddayo.aris.engine.client.ClientInGameEngine
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import net.minecraft.client.Minecraft
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource

@LuaProvider(ClientInGameEngine.PROVIDER, library = "aris.game.sound.client")
object ClientSoundFunction {
    private val mc by lazy { Minecraft.getInstance() }

    /**
     * `sounds.json`에 정의된 Minecraft 소리 이벤트를 클라이언트 플레이어의 현재 위치에서 재생합니다.
     * @param id 소리 이벤트 id. 예: `minecraft:block.note_block.pling`
     */
    @LuaFunction("play_sound")
    fun playSound(id: String) = playSound(id, 1.0, 1.0)

    /**
     * `sounds.json`에 정의된 Minecraft 소리 이벤트를 클라이언트 플레이어의 현재 위치에서 재생합니다.
     * @param id 소리 이벤트 id. 예: `minecraft:block.note_block.pling`
     * @param volume 소리 크기
     * @param pitch 소리 높낮이
     */
    @LuaFunction("play_sound")
    fun playSound(id: String, volume: Double, pitch: Double) {
        val player = mc.player ?: return
        playSoundAt(id, player.x, player.y, player.z, volume, pitch)
    }

    /**
     * `sounds.json`에 정의된 Minecraft 소리 이벤트를 지정한 위치에서 재생합니다.
     * @param id 소리 이벤트 id. 예: `minecraft:block.note_block.pling`
     * @param x x좌표
     * @param y y좌표
     * @param z z좌표
     * @param volume 소리 크기
     * @param pitch 소리 높낮이
     */
    @LuaFunction("play_sound_at")
    fun playSoundAt(id: String, x: Double, y: Double, z: Double, volume: Double, pitch: Double) {
        val level = mc.level ?: return
        level.playLocalSound(
            x,
            y,
            z,
            soundEvent(id),
            SoundSource.PLAYERS,
            volume.toFloat(),
            pitch.toFloat(),
            false
        )
    }

    /**
     * `aris.init.sound.client.register_sound_raw`로 등록한 raw 소리를 재생합니다.
     * @param id raw 소리 이벤트 id. 예: `aris:alert`
     */
    @LuaFunction("play_sound_raw")
    fun playSoundRaw(id: String) = playSoundRaw(id, 1.0, 1.0)

    /**
     * `aris.init.sound.client.register_sound_raw`로 등록한 raw 소리를 재생합니다.
     * @param id raw 소리 이벤트 id. 예: `aris:alert`
     * @param volume 소리 크기
     * @param pitch 소리 높낮이
     */
    @LuaFunction("play_sound_raw")
    fun playSoundRaw(id: String, volume: Double, pitch: Double) {
        val player = mc.player ?: return
        playSoundRawAt(id, player.x, player.y, player.z, volume, pitch)
    }

    /**
     * `aris.init.sound.client.register_sound_raw`로 등록한 raw 소리를 지정한 위치에서 재생합니다.
     * @param id raw 소리 이벤트 id. 예: `aris:alert`
     * @param x x좌표
     * @param y y좌표
     * @param z z좌표
     * @param volume 소리 크기
     * @param pitch 소리 높낮이
     */
    @LuaFunction("play_sound_raw_at")
    fun playSoundRawAt(id: String, x: Double, y: Double, z: Double, volume: Double, pitch: Double) {
        val level = mc.level ?: return
        require(ClientSoundRegistry.hasRaw(id)) { "Raw sound is not registered: $id" }
        ClientSoundRegistry.installRaw(id)
        level.playLocalSound(
            x,
            y,
            z,
            soundEvent(id),
            SoundSource.PLAYERS,
            volume.toFloat(),
            pitch.toFloat(),
            false
        )
    }

    private fun soundEvent(id: String): SoundEvent {
        val location = ResourceLocation.parse(id)
        return BuiltInRegistries.SOUND_EVENT.getValue(location) ?: SoundEvent.createVariableRangeEvent(location)
    }
}
