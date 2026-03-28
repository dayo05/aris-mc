## aris.init.create_item(key: string)
```
 새로운 아이템을 추가합니다.
 @param key 추가할 아이템 id
```
## aris.init.create_particle(key: string)
## aris.init.networking.create_c2s_packet(_id: string) -> C2SPacketDeclaration
```
 패킷을 새로 생성합니다.
 @param _id 패킷 id
 @return 생성된 패킷 정의 Builder
```
## aris.init.networking.create_s2c_packet(_id: string) -> S2CPacketDeclaration
```
 패킷을 새로 생성합니다.
 @param _id 패킷 id
 @return 생성된 패킷 정의 Builder
```
## aris.init.networking.integer_arg(of: string) -> AbstractPackableData
```
 정수 인자를 패킷에 추가합니다.
 @param of 패킷에 첨부할 정수의 이름
 @return 이 함수로 획득한 값을 패킷에 append할 수 있습니다.
```
## aris.init.networking.float_arg(of: string) -> AbstractPackableData
```
 실수 인자를 패킷에 추가합니다.
 @param of 패킷에 첨부할 실수의 이름
 @return 이 함수로 획득한 값을 패킷에 append할 수 있습니다.
```
## aris.init.networking.string_arg(of: string) -> AbstractPackableData
```
 문자열 인자를 패킷에 추가합니다.
 @param of 패킷에 첨부할 문자열의 이름
 @return 이 함수로 획득한 값을 패킷에 append할 수 있습니다.
```
## aris.init.command.sub_command(of: string) -> AbstractCommandHandler
```
 하위 커멘드를 추가합니다.
 @of 추가할 커멘드 이름
 @return 여기에서 획득한 값을 커멘드 핸들러에 append해야합니다.
```
## aris.init.command.integer_arg(of: string) -> AbstractCommandHandler
```
 정수 인수를 추가합니다.
 @of 추가할 정수 인수 이름
 @return 여기에서 획득한 값을 커멘드 핸들러에 append해야합니다.
```
## aris.init.command.float_arg(of: string) -> AbstractCommandHandler
```
 실수 인수를 추가합니다.
 @of 추가할 실수 인수 이름
 @return 여기에서 획득한 값을 커멘드 핸들러에 append해야합니다.
```
## aris.init.command.player_arg(of: string) -> AbstractCommandHandler
```
 플레이어 인수를 추가합니다.
 @of 추가할 플레이어 인수 이름
 @return 여기에서 획득한 값을 커멘드 핸들러에 append해야합니다.
```
## aris.init.command.create_command(of: string) -> AbstractCommandHandler
```
 새로운 명령어를 추가합니다.
 @of 추가할 명령어 이름
```
## aris.init.nbt.from_table(table: any) -> LuaNBTCompound
```
 Converts Lua Table into NBT Compound
```
## aris.init.nbt.from_entity(entity: LuaEntity) -> LuaNBTCompound
```
 Get NBT of entity
 @param entity entity to get nbt
 @return full nbt object of entity
```
## aris.init.nbt.from_item_stack(stack: LuaItemStack) -> LuaNBTCompound
```
 Get NBT of item stack
 @param stack item stack to get nbt
 @return full nbt object of item stack
```
## aris.init.nbt.from_block_entity(level: LuaServerWorld, x: number, y: number, z: number) -> LuaNBTCompound
```
 Get NBT of block entity at specific position
 @param level server level for target block entity
 @param x x position for target block entity
 @param y y position for target block entity
 @param z z position for target block entity
 @return full nbt object of provided location. Nil if not exists.
```
## aris.init.nbt.from_block_state(level: LuaServerWorld, x: number, y: number, z: number) -> LuaNBTCompound
```
 Get NBT of block state at specific position
 @param level server level for target block state
 @param x x position for target block state
 @param y y position for target block state
 @param z z position for target block state
 @return full nbt object of provided location
```
## aris.init.nbt.from_string(string: string) -> LuaNBTCompound
```
 Converts string into NBT
 @param string nbt string to convert into nbt
 @return nbt object of provided string
```


## PacketDeclaration:append(packet: AbstractPackableData)
```
 패킷에 인자를 추가합니다.
 @param packet 추가할 패킷 인자
```


## AbstractCommandHandler:set_endpoint(of: string)
```
 여기에서 설정한 id를 register_endpoint를 통해 등록할 수 있습니다.
 @param of endpoint id
```


## AbstractCommandHandler:append(of: AbstractCommandHandler)


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




