## aris.init.sound.client.register_sound(id: string)
```
 Minecraft 리소스팩의 `sounds.json`에 정의된 소리 이벤트를 등록합니다.
 @param id 소리 이벤트 id. 예: `aris:alert`
```
## aris.init.sound.client.register_sound_raw(id: string, path: string)
```
 `sounds.json` 없이 재생할 raw OGG 소리 이벤트를 등록합니다.
 @param id 등록할 소리 이벤트 id. 예: `aris:alert`
 @param path `assets/sounds` 아래의 상대 경로. 예: `alert.ogg`
```
## aris.init.sound.client.register_sound_raw(id: string, path: string, stream: boolean)
```
 `sounds.json` 없이 재생할 raw OGG 소리 이벤트를 등록합니다.
 @param id 등록할 소리 이벤트 id. 예: `aris:alert`
 @param path `assets/sounds` 아래의 상대 경로. 예: `alert.ogg`
 @param stream 소리를 메모리에 한 번에 올리지 않고 스트리밍할지 여부
```
## aris.init.client.create_keybinding(key: string, code: number, category: string)
```
 새로운 조작키를 추가합니다.
 @param key key 이름
 @param code keycode, https://www.glfw.org/docs/3.3/group__keys.html 에서 찾을 수 있습니다.
 @param category key 카테고리
```
## aris.init.client.create_particle_info(key: string) -> ParticleInfo


## ParticleInfo:set_lifetime(new_value: number)


## ParticleInfo:get_lifetime() -> number


## ParticleInfo:set_has_physics(new_value: boolean)


## ParticleInfo:get_has_physics() -> boolean


## ParticleInfo:set_friction(new_value: number)


## ParticleInfo:get_friction() -> number


## ParticleInfo:set_quadSize(new_value: number)


## ParticleInfo:get_quadSize() -> number


## ParticleInfo:set_r(new_value: number)


## ParticleInfo:get_r() -> number


## ParticleInfo:set_g(new_value: number)


## ParticleInfo:get_g() -> number


## ParticleInfo:set_b(new_value: number)


## ParticleInfo:get_b() -> number


## ParticleInfo:set_enable_random_movement(new_value: boolean)


## ParticleInfo:get_enable_random_movement() -> boolean
