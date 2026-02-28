## aris.game.add_on_use_item(item: string, func: function)
```
 추가한 아이템을 사용했을때 실행할 함수를 추가합니다.
 @param item 아이템 id
 @param func 실행할 함수
```
## aris.game.clear_on_use_item(item: string)
```
 add_on_use_item을 통해 등록한 함수들을 초기화합니다.
 @param item 초기화할 아이템
```
## aris.game.add_on_right_click_hook(f: function)
```
 플레이어가 임의의 위치를 우클릭시 실행할 함수
 @param f 실행할 함수
```
## aris.game.add_tick_hook(f: function)
```
 매 틱마다 실행할 함수를 추가합니다.
 @param f 실행할 함수
```
## aris.game.dispatch_command(command: string)
```
 서버 콘솔에서 커멘드를 실행합니다.
 @param command 실행할 명령어
```
## aris.game.create_effect_builder(of: string) -> LuaMobEffectInstance
## aris.game.create_effect_builder(ns: string, of: string) -> LuaMobEffectInstance
## aris.game.summon_entity(entityType: LuaEntityType, world: LuaServerWorld, pos: Point3) -> LuaEntity
## aris.game.entity_type_of(str: string) -> LuaEntityType
## aris.game.iter_players(fn: function) -> 
```
 모든 플레이어를 한번씩 callback으로 넘겨줍니다.
 플레이어 리스트에서 for문을 돌리는 것과 유사합니다.
 @param fn callback
```
## aris.game.hook.add_on_use_item(item: string, func: function)
```
 추가한 아이템을 사용했을때 실행할 함수를 추가합니다.
 @param item 아이템 id
 @param func 실행할 함수
```
## aris.game.hook.clear_on_use_item(item: string)
```
 add_on_use_item을 통해 등록한 함수들을 초기화합니다.
 @param item 초기화할 아이템
```
## aris.game.hook.add_on_right_click(f: function)
```
 플레이어가 임의의 위치를 우클릭시 실행할 함수
 @param f 실행할 함수
```
## aris.game.hook.clear_on_right_click()
```
 플레이어가 임의의 위치를 우클릭시 실행할 훅 초기화
```
## aris.game.hook.add_on_entity_damaged(f: function)
```
 플레이어가 데미지를 입었을 때 실행할 함수
 @param f 실행할 함수
```
## aris.game.hook.register_endpoint(of: string, func: function)
```
 명령어를 입력했을때 실행할 함수를 지정합니다.
 @param of 명령어 id
 @param func 실행할 함수
```
## aris.game.hook.add_c2s_packet_handler(id: string, func: function)
```
 패킷이 클라이언트로부터 전송됐을때 실행할 함수를 지정합니다.
 @param id 패킷 id
 @param func 실행할 함수
```
## aris.game.hook.add_tick(f: function)
```
 매 틱마다 실행할 함수를 추가합니다.
 @param f 실행할 함수
```
## aris.game.hook.clear_tick(f: function)
```
 매 틱마다 실행할 함수를 초기화합니다.
```
## aris.game.networking.register_c2s_packet_handler(id: string, func: function)
```
 패킷이 클라이언트로부터 전송됐을때 실행할 함수를 지정합니다.
 @param id 패킷 id
 @param func 실행할 함수
```
## aris.game.networking.send_s2c_packet(player: LuaServerPlayer, packet: PacketDeclaration.Builder)
```
 클라이언트로 주어진 패킷을 전송합니다.
 @param player 타겟 플레이어
 @param packet 패킷
```
## aris.game.networking.create_s2c_packet_builder(of: string) -> PacketDeclaration.Builder
```
 클라이언트로 전송할 패킷을 설정하는 빌더(builder)를 만듭니다.
 @param of 전송할 패킷의 id
```
## aris.game.world.get_world(world: string) -> LuaServerWorld
## get_overworld() -> LuaServerWorld
## get_nether() -> LuaServerWorld
## get_end() -> LuaServerWorld
## aris.game.nbt.from_table(table: any) -> LuaNBTCompound
```
 Converts Lua Table into NBT Compound
```
## aris.game.nbt.from_entity(entity: LuaEntity) -> LuaNBTCompound
```
 Get NBT of entity
 @param entity entity to get nbt
 @return full nbt object of entity
```
## aris.game.nbt.from_item_stack(stack: LuaItemStack) -> LuaNBTCompound
```
 Get NBT of item stack
 @param stack item stack to get nbt
 @return full nbt object of item stack
```
## aris.game.nbt.from_block_entity(level: LuaServerWorld, x: number, y: number, z: number) -> LuaNBTCompound
```
 Get NBT of block entity at specific position
 @param level server level for target block entity
 @param x x position for target block entity
 @param y y position for target block entity
 @param z z position for target block entity
 @return full nbt object of provided location. Nil if not exists.
```
## aris.game.nbt.from_block_state(level: LuaServerWorld, x: number, y: number, z: number) -> LuaNBTCompound
```
 Get NBT of block state at specific position
 @param level server level for target block state
 @param x x position for target block state
 @param y y position for target block state
 @param z z position for target block state
 @return full nbt object of provided location
```
## aris.game.nbt.from_string(string: string) -> LuaNBTCompound
```
 Converts string into NBT
 @param string nbt string to convert into nbt
 @return nbt object of provided string
```
## aris.game.command.register_endpoint(of: string, func: function)
```
 명령어를 입력했을때 실행할 함수를 지정합니다.
 @param of 명령어 id
 @param func 실행할 함수
```


## Builder:append_int(id: string, of: number)
```
 정수 인자를 패킷에 추가합니다.
 @param id 패킷에 첨부할 정수의 이름
 @param of 추가할 정수
```


## Builder:append_string(id: string, of: string)
```
 문자열 인자를 패킷에 추가합니다.
 @param id 패킷에 첨부할 문자열의 이름
 @param of 추가할 문자열
```


## Builder:append_float(id: string, of: number)
```
 실수 인자를 패킷에 추가합니다.
 @param id 패킷에 첨부할 실수의 이름
 @param of 추가할 실수
```




## LuaDamageSource:set_amount(new_value: number)


## LuaDamageSource:get_amount() -> number


## LuaDamageSource:get_causing() -> LuaEntity


## LuaDamageSource:get_direct() -> LuaEntity


## LuaDamageSource:get_isDirect() -> boolean


## LuaDamageSource:get_id() -> string


## LuaMobEffectInstance:set_duration(new_value: number)
```
 Duration(tick)
```


## LuaMobEffectInstance:get_duration() -> number
```
 Duration(tick)
```


## LuaMobEffectInstance:set_amplifier(new_value: number)


## LuaMobEffectInstance:get_amplifier() -> number


## LuaMobEffectInstance:set_ambient(new_value: boolean)
```
 거품 표시 여부
```


## LuaMobEffectInstance:get_ambient() -> boolean
```
 거품 표시 여부
```


## LuaMobEffectInstance:set_visible(new_value: boolean)


## LuaMobEffectInstance:get_visible() -> boolean


## LuaMobEffectInstance:set_showIcon(new_value: boolean)


## LuaMobEffectInstance:get_showIcon() -> boolean


## LuaItemStack:set_count(new_value: number)
```
 해당 ItemStack의 수량을 설정하거나 가져옵니다.
```


## LuaItemStack:get_count() -> number
```
 해당 ItemStack의 수량을 설정하거나 가져옵니다.
```


## LuaItemStack:get_display_name() -> string
```
 해당 아이템의 표기된 이름을 가져옵니다.
```


## LuaItemStack:get_name() -> string
```
 해당 아이템의 기본 이름을 가져옵니다.
```


## LuaNBTCompound:into_string() -> string
```
 Convert NBT into JSON string
```


## LuaNBTCompound:into_table() -> any
```
 Convert NBT into Lua Table
```


## LuaNBTCompound:into_item_stack() -> LuaItemStack
```
 Convert NBT into item stack
```


## LuaNBTCompound:apply_entity(entity: LuaEntity)
```
 Apply(overwrite) current NBT into entity
```


## LuaNBTCompound:spawn_entity(level: LuaServerWorld) -> LuaEntity
```
 Spawn entity with this NBT
```


## LuaNBTCompound:place_block_entity(level: LuaServerWorld) -> boolean


## LuaNBTCompound:place_block_state(level: LuaServerWorld, x: number, y: number, z: number) -> boolean
```
 Place block with this NBT at provided position
 If exists then it replaces
 @return is successful
```




## LuaEntity:add_damage(damage: number)
```
 엔티티에 특정 데미지를 줄 수 있습니다.
```


## LuaEntity:add_velocity(x: number, y: number, z: number)


## LuaEntity:add_velocity_relative(x: number, y: number, z: number)


## LuaEntity:move_delta(x: number, y: number, z: number)
```
 엔티티를 특정 상대적인 위치로 텔레포트 시킵니다.
 @param x 이동시킬 x좌표의 상대적인 값
 @param y 이동시킬 y좌표의 상대적인 값
 @param z 이동시킬 z좌표의 상대적인 값
```


## LuaEntity:move_to(x: number, y: number, z: number)
```
 엔티티를 특정 위치로 텔레포트 시킵니다.
 @param x 이동시킬 x좌표
 @param y 이동시킬 y좌표
 @param z 이동시킬 z좌표
```


## LuaEntity:move_delta_relative(x: number, y: number, z: number)
```
 엔티티를 바라보는 위치를 기준으로 하는 상대적인 위치로 텔레포트 시킵니다.
 @param x 앞으로 이동할 칸수
 @param y 위로 이동할 칸수
 @param z 옆으로 이동할 칸수(+는 오른쪽을 의미)
```


## LuaEntity:iter_entities_nearby(fn: function, radius: number, includeSelf: boolean) -> 


## LuaEntity:get_name() -> string
```
 엔티티의 이름을 가져옵니다.
```


## LuaEntity:get_type() -> string


## LuaEntity:get_display_name() -> string
```
 엔티티의 표시된 이름을 가져옵니다.
```


## LuaEntity:set_custom_name(new_value: string)
```
 엔티티의 커스텀 이름을 설정하거나 가져올 수 있습니다.
```


## LuaEntity:get_custom_name() -> string
```
 엔티티의 커스텀 이름을 설정하거나 가져올 수 있습니다.
```


## LuaEntity:get_entity_type() -> LuaEntityType


## LuaEntity:set_x(new_value: number)
```
 플레이어의 X좌표를 가져오거나 설정할 수 있습니다.
```


## LuaEntity:get_x() -> number
```
 플레이어의 X좌표를 가져오거나 설정할 수 있습니다.
```


## LuaEntity:set_y(new_value: number)
```
 엔티티의 Y좌표를 가져오거나 설정할 수 있습니다.
```


## LuaEntity:get_y() -> number
```
 엔티티의 Y좌표를 가져오거나 설정할 수 있습니다.
```


## LuaEntity:set_z(new_value: number)
```
 엔티티의 Z좌표를 가져오거나 설정할 수 있습니다.
```


## LuaEntity:get_z() -> number
```
 엔티티의 Z좌표를 가져오거나 설정할 수 있습니다.
```


## LuaEntity:get_uuid() -> string
```
 엔티티의 uuid를 가져옵니다.
```


## LuaEntity:get_server_world() -> LuaServerWorld
```
 엔티티의 world를 가져옵니다.
```


## LuaLivingEntity:add_effect(effect: LuaMobEffectInstance)


## LuaLivingEntity:clear_effect()


## LuaLivingEntity:remove_effect(of: string)
## LuaLivingEntity:remove_effect(ns: string, of: string)


## LuaLivingEntity:set_pitch(new_value: number)


## LuaLivingEntity:get_pitch() -> number


## LuaLivingEntity:set_yaw(new_value: number)


## LuaLivingEntity:get_yaw() -> number


## LuaPlayerEntity:get_main_hand_item() -> LuaItemStack
```
 플레이어의 오른손의 아이템을 가져옵니다.
```


## LuaServerPlayer:send_message_text(msg: string)
```
 채팅으로 텍스트 메시지를 전송
 @param msg 전송할 텍스트
```


## LuaServerPlayer:send_message(msg: Component)


## LuaServerPlayer:iter_player_nearby(fn: function, lnt: number, includeSelf: boolean) -> 
```
 플레이어로부터 유클리드 거리(직선거리) 기준 특정 거리 이내인 플레이어를 탐색합니다.
 @param fn callback
 @param lnt 플레이어로부터의 거리
 @param includeSelf true인 경우 자기 자신을 포함하고, false인 경우 제외합니다.
 @see aris.game.iter_players
```


## LuaServerPlayer:set_main_hand_item(new_value: LuaItemStack)


## LuaServerPlayer:get_main_hand_item() -> LuaItemStack
