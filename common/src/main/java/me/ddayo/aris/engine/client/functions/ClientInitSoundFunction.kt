package me.ddayo.aris.engine.client.functions

import me.ddayo.aris.client.sound.ClientSoundRegistry
import me.ddayo.aris.engine.client.ClientInitEngine
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider

@LuaProvider(ClientInitEngine.PROVIDER, library = "aris.init.sound.client")
object ClientInitSoundFunction {
    /**
     * Minecraft 리소스팩의 `sounds.json`에 정의된 소리 이벤트를 등록합니다.
     * @param id 소리 이벤트 id. 예: `aris:alert`
     */
    @LuaFunction("register_sound")
    fun registerSound(id: String) = ClientSoundRegistry.register(id)

    /**
     * `sounds.json` 없이 재생할 raw OGG 소리 이벤트를 등록합니다.
     * @param id 등록할 소리 이벤트 id. 예: `aris:alert`
     * @param path `assets/sounds` 아래의 상대 경로. 예: `alert.ogg`
     */
    @LuaFunction("register_sound_raw")
    fun registerSoundRaw(id: String, path: String) = ClientSoundRegistry.registerRaw(id, path)

    /**
     * `sounds.json` 없이 재생할 raw OGG 소리 이벤트를 등록합니다.
     * @param id 등록할 소리 이벤트 id. 예: `aris:alert`
     * @param path `assets/sounds` 아래의 상대 경로. 예: `alert.ogg`
     * @param stream 소리를 메모리에 한 번에 올리지 않고 스트리밍할지 여부
     */
    @LuaFunction("register_sound_raw")
    fun registerSoundRaw(id: String, path: String, stream: Boolean) = ClientSoundRegistry.registerRaw(id, path, stream)
}
