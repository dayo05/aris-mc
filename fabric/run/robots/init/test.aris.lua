local cmd = aris.init.command

-- /aris 루트 명령어 아래에 test 하위 명령어를 둡니다. (/aris test ...)
local aris_command = cmd.create_command("aris")
local test_command = cmd.sub_command("test")
aris_command:append(test_command)

-- /aris test equip
local equip_sub = cmd.sub_command("equip")
equip_sub:set_endpoint("test_equip")
test_command:append(equip_sub)

-- /aris test remove
local remove_sub = cmd.sub_command("remove")
remove_sub:set_endpoint("test_remove")
test_command:append(remove_sub)

-- /aris test nbt
local nbt_sub = cmd.sub_command("nbt")
nbt_sub:set_endpoint("test_nbt")
test_command:append(nbt_sub)

-- /aris test clear
local clear_sub = cmd.sub_command("clear")
clear_sub:set_endpoint("test_clear")
test_command:append(clear_sub)

-- /aris test suggest <fruit> : dynamic list autocomplete test
local suggest_sub = cmd.sub_command("suggest")
local suggest_fruit_arg = cmd.suggested_word_arg("fruit", "test_fruits")
suggest_fruit_arg:set_endpoint("test_suggest")
suggest_sub:append(suggest_fruit_arg)
test_command:append(suggest_sub)

-- /aris test slot
local slot_sub = cmd.sub_command("slot")
slot_sub:set_endpoint("test_slot")
test_command:append(slot_sub)

-- /aris test gui : 클라이언트 테스트 GUI 열기 (텍스트 입력, 체크박스, 아이템 툴팁, on_close_hook 테스트)
local gui_sub = cmd.sub_command("gui")
gui_sub:set_endpoint("test_gui")
test_command:append(gui_sub)

-- /aris test health : 엔티티 체력 함수 테스트
local health_sub = cmd.sub_command("health")
health_sub:set_endpoint("test_health")
test_command:append(health_sub)

-- /aris test damage_cancel on|off|count : add_on_entity_damaged cancel test
local damage_cancel_sub = cmd.sub_command("damage_cancel")
test_command:append(damage_cancel_sub)

local damage_cancel_on_sub = cmd.sub_command("on")
damage_cancel_on_sub:set_endpoint("damage_cancel_on")
damage_cancel_sub:append(damage_cancel_on_sub)

local damage_cancel_off_sub = cmd.sub_command("off")
damage_cancel_off_sub:set_endpoint("damage_cancel_off")
damage_cancel_sub:append(damage_cancel_off_sub)

local damage_cancel_count_sub = cmd.sub_command("count")
damage_cancel_count_sub:set_endpoint("damage_cancel_count")
damage_cancel_sub:append(damage_cancel_count_sub)

-- /aris test event_test_cancel_on : 이벤트 취소 테스트 활성화
local event_test_cancel_on_sub = cmd.sub_command("event_test_cancel_on")
event_test_cancel_on_sub:set_endpoint("event_test_cancel_on")
test_command:append(event_test_cancel_on_sub)

-- /aris test event_test_cancel_off : 이벤트 취소 테스트 비활성화
local event_test_cancel_off_sub = cmd.sub_command("event_test_cancel_off")
event_test_cancel_off_sub:set_endpoint("event_test_cancel_off")
test_command:append(event_test_cancel_off_sub)

-- /aris test event_test_summary : 이벤트 호출 횟수 확인
local event_test_summary_sub = cmd.sub_command("event_test_summary")
event_test_summary_sub:set_endpoint("event_test_summary")
test_command:append(event_test_summary_sub)

-- /aris test selector self : Lua selector function smoke test
local selector_sub = cmd.sub_command("selector")
test_command:append(selector_sub)

local selector_self_sub = cmd.sub_command("self")
selector_self_sub:set_endpoint("test_selector_self")
selector_sub:append(selector_self_sub)

-- /aris test selector entity <target> : 단일 엔티티 selector argument 테스트
local selector_entity_sub = cmd.sub_command("entity")
local selector_entity_arg = cmd.entity_arg("target")
selector_entity_arg:set_endpoint("test_selector_entity_arg")
selector_entity_sub:append(selector_entity_arg)
selector_sub:append(selector_entity_sub)

-- /aris test selector entities <targets> : 복수 엔티티 selector argument 테스트
local selector_entities_sub = cmd.sub_command("entities")
local selector_entities_arg = cmd.entities_arg("targets")
selector_entities_arg:set_endpoint("test_selector_entities_arg")
selector_entities_sub:append(selector_entities_arg)
selector_sub:append(selector_entities_sub)

-- /aris test selector players <players> : 복수 플레이어 selector argument 테스트
local selector_players_sub = cmd.sub_command("players")
local selector_players_arg = cmd.players_arg("players")
selector_players_arg:set_endpoint("test_selector_players_arg")
selector_players_sub:append(selector_players_arg)
selector_sub:append(selector_players_sub)

-- ===== 새 기능 테스트용 패킷 선언 =====
local net = aris.init.networking

-- 서버 -> 클라이언트: 테스트 GUI 열기 + 아이템스택 전송 (itemstack S2C 테스트)
local open_test_gui_s2c = net.create_s2c_packet("open_test_gui")
open_test_gui_s2c:append(net.string_arg("title"))
open_test_gui_s2c:append(net.itemstack_arg("item"))

-- 클라이언트 -> 서버: GUI 제출 + 아이템스택 전송 (itemstack C2S 테스트)
local test_gui_submit_c2s = net.create_c2s_packet("test_gui_submit")
test_gui_submit_c2s:append(net.string_arg("text"))
test_gui_submit_c2s:append(net.itemstack_arg("item"))
