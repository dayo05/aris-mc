## aris.game.client.send_system_message(message: string)
```
 채팅창에 새로운 텍스트를 추가합니다.
 @param message 추가할 메시지
```
## aris.game.client.invoke_command(command: string)
```
 채팅창에 /command를 입력하는 것과 동일합니다.
 @param command 실행할 커멘드
```
## aris.game.client.get_player_x() -> number
```
 현재 플레이어의 x좌표를 구합니다.
```
## aris.game.client.get_player_y() -> number
```
 현재 플레이어의 y좌표를 구합니다.
```
## aris.game.client.get_player_z() -> number
## aris.game.client.get_player_pitch() -> number
## aris.game.client.get_player_yaw() -> number
## aris.game.client.item_used_duration() -> number
```
 플레이어가 얼마나 오랫동안 아이템을 사용했는지(charging)
 @return 플레이어가 차징한 시간(tick)
```
## aris.game.client.create_hud() -> HudRenderer
```
 HUD를 생성합니다.
```
## aris.game.client.clear_opened_hud()
```
 모든 열려있는 HUD를 닫습니다.
```
## aris.game.client.remote_string_data(of: string) -> string
```
 서버로부터 전송받은 문자열 데이터를 가져옵니다.
```
## aris.game.client.remote_number_data(of: string) -> number
```
 서버로부터 전송받은 정수 데이터를 가져옵니다.
```
## aris.game.client.remove_item_data(of: string) -> LuaItemStack
```
 서버로부터 전송받은 아이템 데이터를 가져옵니다.
```
## aris.game.client.is_key_pressed(key: number) -> boolean
```
 특정 키가 눌린 상태인지 검사합니다.
 이 함수는 씹힐 위험이 있으니, 사용을 지양하세요.
 @param key 눌려져있는지 확인할 키
```
## aris.game.client.target_crosshair_entity(reach: number) -> LuaEntity
## aris.game.client.hook.add_s2c_packet_handler(id: string, func: function)
```
 패킷이 서버로부터 전송됐을때 실행할 함수를 지정합니다.
 @param id 패킷 id
 @param func 실행할 함수
```
## aris.game.client.networking.send_c2s_packet(packet: PacketDeclaration.Builder)
```
 서버로 주어진 패킷을 전송합니다.
 @param packet 패킷
```
## aris.game.client.networking.create_c2s_packet_builder(of: string) -> PacketDeclaration.Builder
```
 서버로 전송할 패킷을 설정하는 빌더(builder)를 만듭니다.
 @param of 전송할 패킷의 id
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


## HudRenderer:open_hud()
```
 Registers this renderer to the in-game HUD engine, making it visible on the HUD.
```


## HudRenderer:close_hud()
```
 Unregisters this renderer from the in-game HUD engine.
```
