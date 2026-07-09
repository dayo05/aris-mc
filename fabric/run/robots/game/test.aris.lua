local game = aris.game
local hook = aris.game.hook
local nbt = aris.game.nbt
local networking = aris.game.networking
local suggestion = aris.game.command.suggestion

-- 아이템 지급/제거 테스트
hook.add_on_right_click(function(event)
    local player = event:get_player()
    if not player:get_is_sneaking() then
        return
    end

    -- 웅크린 상태에서 우클릭 시 다이아몬드 5개 지급
    player:give_item("minecraft:diamond", 5)
    aris.log_info(player:get_name() .. " 에게 다이아몬드 5개 지급!")
end)

-- 아이템 이동 감지 및 취소 테스트
hook.add_on_item_move(function(event)
    local item = event:get_item()
    local player = event:get_player()
    local type = event:get_type()

    -- 다이아몬드 드롭 방지
    if type == "drop" and item:get_name() == "[Diamond]" then
        aris.log_info("다이아몬드 드롭 차단: " .. player:get_name())
        event:cancel()
        return
    end

    if type == "drop" then
        aris.log_info("[ItemMove] " .. player:get_name() .. " " .. type .. " " .. item:get_display_name() .. " x" .. tostring(item:get_count()))
    end
end)

-- 엔티티 장비 테스트 커맨드
hook.register_endpoint("test_equip", function(player, arg)
    -- 각 장비를 올바른 슬롯에 장착 (set_equipment 는 슬롯을 명시하므로 안전합니다)
    player:set_equipment("head", game.create_item("minecraft:diamond_helmet", 1))
    player:set_equipment("chest", game.create_item("minecraft:diamond_chestplate", 1))
    player:set_equipment("legs", game.create_item("minecraft:diamond_leggings", 1))
    player:set_equipment("feet", game.create_item("minecraft:diamond_boots", 1))
    player:set_equipment("mainhand", game.create_item("minecraft:diamond_sword", 1))
    player:send_message_text("다이아몬드 장비 장착 완료!")
end)

-- 아이템 제거 테스트 커맨드
hook.register_endpoint("test_remove", function(player, arg)
    local success = player:remove_item("minecraft:diamond", 3)
    if success then
        player:send_message_text("다이아몬드 3개 제거 성공")
    else
        player:send_message_text("다이아몬드가 3개 이상 없습니다")
    end
end)

-- NBT 커스텀 데이터 테스트 커맨드
hook.register_endpoint("test_nbt", function(player, arg)
    local item = player:get_main_hand_item()
    if item:get_count() == 0 then
        player:send_message_text("손에 아이템을 들어주세요")
        return
    end

    -- 커스텀 데이터 쓰기
    local data = nbt.from_table({ my_key = "hello", my_number = 42 })
    item:set_data(data)

    -- 커스텀 데이터 읽기
    local read_data = item:get_data():into_table()
    player:send_message_text("data.my_key = " .. tostring(read_data.my_key))
    player:send_message_text("data.my_number = " .. tostring(read_data.my_number))
end)

-- 인벤토리 초기화 커맨드
hook.register_endpoint("test_clear", function(player, arg)
    player:clear_inventory()
    player:send_message_text("인벤토리 초기화 완료!")
end)

-- /aris test suggest <fruit> : dynamic list autocomplete test
suggestion.set_list("test_fruits", {"apple", "banana", "blueberry", "cherry"})

hook.register_endpoint("test_suggest", function(player, arg)
    player:send_message_text("자동완성 선택값: " .. tostring(arg.fruit))
    suggestion.set_list("test_fruits", {"dragonfruit", "durian", "grape", "melon"})
    player:send_message_text("자동완성 리스트를 갱신했습니다. /aris test suggest d<TAB> 를 확인하세요.")
end)

-- 슬롯 접근 테스트 커맨드
hook.register_endpoint("test_slot", function(player, arg)
    -- 0번 슬롯(핫바 첫칸) 아이템 정보 출력
    local slot_item = player:get_slot(0)
    if slot_item:get_count() > 0 then
        player:send_message_text("슬롯 0: " .. slot_item:get_display_name() .. " x" .. tostring(slot_item:get_count()))
    else
        player:send_message_text("슬롯 0: 비어있음")
    end
end)

-- 좌클릭 감지 테스트
hook.add_on_left_click(function(event)
    local player = event:get_player()
    local sneaking = player:get_is_sneaking()
    local running = player:get_is_running()
    local status = ""
    if sneaking then status = status .. " [SNEAKING]" end
    if running then status = status .. " [RUNNING]" end
    player:send_message_text("Left click detected!" .. status)
end)

-- ===== 새 기능 테스트 =====

-- /aris test health : get_health / get_max_health / set_health 테스트
hook.register_endpoint("test_health", function(player, arg)
    player:send_message_text("현재 체력: " .. tostring(player:get_health()) .. " / " .. tostring(player:get_max_health()))
    player:set_health(player:get_max_health())
    player:send_message_text("체력을 최대로 회복했습니다!")
end)

-- /aris test damage_cancel on|off|count : add_on_entity_damaged cancel test
local damage_cancel_enabled = false
local damage_cancel_count = 0

hook.add_on_entity_damaged(function(event)
    damage_cancel_count = damage_cancel_count + 1

    if damage_cancel_enabled then
        event:cancel()
    end
end)

hook.register_endpoint("damage_cancel_on", function(player, arg)
    damage_cancel_enabled = true
    player:send_message_text("damage cancel: ON")
end)

hook.register_endpoint("damage_cancel_off", function(player, arg)
    damage_cancel_enabled = false
    player:send_message_text("damage cancel: OFF")
end)

hook.register_endpoint("damage_cancel_count", function(player, arg)
    player:send_message_text("damage hook calls: " .. tostring(damage_cancel_count))
end)

-- /aris test gui : 클라이언트에 테스트 GUI 열기 패킷 전송 (아이템스택 S2C 테스트)
hook.register_endpoint("test_gui", function(player, arg)
    local builder = networking.create_s2c_packet_builder("open_test_gui")
    builder:append_string("title", "Aris Test GUI")
    builder:append_itemstack("item", player:get_main_hand_item())
    networking.send_s2c_packet(player, builder)
end)

-- 클라이언트가 제출한 GUI 데이터 수신 (아이템스택 C2S 테스트)
hook.add_c2s_packet_handler("test_gui_submit", function(player, v)
    player:send_message_text("입력한 텍스트: " .. v.text)
    player:send_message_text("전송한 아이템: " .. v.item:get_display_name() .. " x" .. tostring(v.item:get_count()))
end)

-- ===== 이벤트 훅 실제 환경 테스트 =====

local event_test_cancel_enabled = false
local event_test_counts = {
    block_left_click = 0,
    block_right_click = 0,
    block_place = 0,
    block_break = 0,
    entity_interact = 0,
    entity_attack = 0,
    player_death = 0,
    player_respawn = 0,
    item_consume = 0,
    chat = 0,
}

local function event_test_log(player, message)
    aris.log_info("[event-test] " .. message)
    if player ~= nil then
        player:send_message_text("[event-test] " .. message)
    end
end

hook.register_endpoint("event_test_cancel_on", function(player, arg)
    event_test_cancel_enabled = true
    player:send_message_text("event test cancel: ON")
end)

hook.register_endpoint("event_test_cancel_off", function(player, arg)
    event_test_cancel_enabled = false
    player:send_message_text("event test cancel: OFF")
end)

hook.register_endpoint("event_test_summary", function(player, arg)
    player:send_message_text("block_left_click=" .. tostring(event_test_counts.block_left_click))
    player:send_message_text("block_right_click=" .. tostring(event_test_counts.block_right_click))
    player:send_message_text("block_place=" .. tostring(event_test_counts.block_place))
    player:send_message_text("block_break=" .. tostring(event_test_counts.block_break))
    player:send_message_text("entity_interact=" .. tostring(event_test_counts.entity_interact))
    player:send_message_text("entity_attack=" .. tostring(event_test_counts.entity_attack))
    player:send_message_text("player_death=" .. tostring(event_test_counts.player_death))
    player:send_message_text("player_respawn=" .. tostring(event_test_counts.player_respawn))
    player:send_message_text("item_consume=" .. tostring(event_test_counts.item_consume))
    player:send_message_text("chat=" .. tostring(event_test_counts.chat))
end)

local function selector_test_assert(player, condition, message)
    if not condition then
        aris.log_error("[selector-test] FAIL: " .. message)
        player:send_message_text("[selector-test] FAIL: " .. message)
        return false
    end
    return true
end

local function selector_test_pass(player, message)
    aris.log_info("[selector-test] PASS: " .. message)
    player:send_message_text("[selector-test] PASS: " .. message)
end

-- /aris test selector self
hook.register_endpoint("test_selector_self", function(player, arg)
    local selected_self = game.get_selector_one_from(player, "@s")
    if not selector_test_assert(player, selected_self ~= nil, "get_selector_one_from(player, '@s') returned nil") then
        return
    end
    if not selector_test_assert(player, selected_self:get_uuid() == player:get_uuid(), "@s did not resolve to the executing player") then
        return
    end

    local self_count = 0
    game.iter_selector_from(player, "@s", function(entity)
        self_count = self_count + 1
        selector_test_assert(player, entity:get_uuid() == player:get_uuid(), "iter_selector_from('@s') returned another entity")
    end)
    if not selector_test_assert(player, self_count == 1, "iter_selector_from('@s') count was " .. tostring(self_count)) then
        return
    end

    local any_entity_count = 0
    game.iter_selector("@e[limit=1]", function(entity)
        any_entity_count = any_entity_count + 1
    end)
    if not selector_test_assert(player, any_entity_count == 1, "iter_selector('@e[limit=1]') count was " .. tostring(any_entity_count)) then
        return
    end

    selector_test_pass(player, "function selector APIs")
end)

-- /aris test selector entity <target>
hook.register_endpoint("test_selector_entity_arg", function(player, arg)
    if not selector_test_assert(player, arg.target ~= nil, "entity_arg target was nil") then
        return
    end
    player:send_message_text("[selector-test] entity_arg target=" .. arg.target:get_name())
    selector_test_pass(player, "entity_arg")
end)

-- /aris test selector entities <targets>
hook.register_endpoint("test_selector_entities_arg", function(player, arg)
    if not selector_test_assert(player, arg.targets ~= nil, "entities_arg targets was nil") then
        return
    end
    if not selector_test_assert(player, arg.targets:get_size() > 0, "entities_arg returned an empty list") then
        return
    end

    local iter_count = 0
    arg.targets:iter(function(entity)
        iter_count = iter_count + 1
    end)
    if not selector_test_assert(player, iter_count == arg.targets:get_size(), "LuaEntityList iter count did not match size") then
        return
    end

    local first = arg.targets:get(1)
    if not selector_test_assert(player, first ~= nil, "LuaEntityList get(1) returned nil") then
        return
    end
    player:send_message_text("[selector-test] entities_arg size=" .. tostring(arg.targets:get_size()) .. " first=" .. first:get_name())
    selector_test_pass(player, "entities_arg")
end)

-- /aris test selector players <players>
hook.register_endpoint("test_selector_players_arg", function(player, arg)
    if not selector_test_assert(player, arg.players ~= nil, "players_arg players was nil") then
        return
    end
    if not selector_test_assert(player, arg.players:get_size() > 0, "players_arg returned an empty list") then
        return
    end

    local found_self = false
    arg.players:iter(function(entity)
        if entity:get_uuid() == player:get_uuid() then
            found_self = true
        end
    end)
    if not selector_test_assert(player, found_self, "players_arg did not include the executing player") then
        return
    end

    selector_test_pass(player, "players_arg")
end)

hook.add_on_block_left_click(function(event)
    event_test_counts.block_left_click = event_test_counts.block_left_click + 1
    local player = event:get_player()
    event_test_log(player, "block_left_click #" .. tostring(event_test_counts.block_left_click)
        .. " block=" .. event:get_block_id()
        .. " pos=" .. tostring(event:get_x()) .. "," .. tostring(event:get_y()) .. "," .. tostring(event:get_z())
        .. " face=" .. event:get_face())
end)

hook.add_on_block_right_click(function(event)
    event_test_counts.block_right_click = event_test_counts.block_right_click + 1
    local player = event:get_player()
    event_test_log(player, "block_right_click #" .. tostring(event_test_counts.block_right_click)
        .. " block=" .. event:get_block_id()
        .. " pos=" .. tostring(event:get_x()) .. "," .. tostring(event:get_y()) .. "," .. tostring(event:get_z())
        .. " face=" .. event:get_face()
        .. " hand=" .. event:get_hand())
end)

hook.add_on_block_place(function(event)
    event_test_counts.block_place = event_test_counts.block_place + 1
    local player = event:get_player()
    event_test_log(player, "block_place #" .. tostring(event_test_counts.block_place)
        .. " block=" .. event:get_block_id()
        .. " pos=" .. tostring(event:get_x()) .. "," .. tostring(event:get_y()) .. "," .. tostring(event:get_z())
        .. " face=" .. event:get_face()
        .. " hand=" .. event:get_hand())

    if event_test_cancel_enabled and event:get_block_id() == "minecraft:tnt" then
        event:cancel()
        event_test_log(player, "minecraft:tnt placement cancelled")
    end
end)

hook.add_on_block_break(function(event)
    event_test_counts.block_break = event_test_counts.block_break + 1
    local player = event:get_player()
    event_test_log(player, "block_break #" .. tostring(event_test_counts.block_break)
        .. " block=" .. event:get_block_id()
        .. " pos=" .. tostring(event:get_x()) .. "," .. tostring(event:get_y()) .. "," .. tostring(event:get_z()))

    if event_test_cancel_enabled and event:get_block_id() == "minecraft:diamond_block" then
        event:cancel()
        event_test_log(player, "minecraft:diamond_block break cancelled")
    end
end)

hook.add_on_entity_interact(function(event)
    event_test_counts.entity_interact = event_test_counts.entity_interact + 1
    local player = event:get_player()
    local target = event:get_target()
    event_test_log(player, "entity_interact #" .. tostring(event_test_counts.entity_interact)
        .. " target=" .. target:get_name()
        .. " type=" .. target:get_type()
        .. " hand=" .. event:get_hand())
end)

hook.add_on_entity_attack(function(event)
    event_test_counts.entity_attack = event_test_counts.entity_attack + 1
    local player = event:get_player()
    local target = event:get_target()
    event_test_log(player, "entity_attack #" .. tostring(event_test_counts.entity_attack)
        .. " target=" .. target:get_name()
        .. " type=" .. target:get_type())

    if event_test_cancel_enabled and target:get_type() == "minecraft:villager" then
        event:cancel()
        event_test_log(player, "villager attack cancelled")
    end
end)

hook.add_on_player_death(function(event)
    event_test_counts.player_death = event_test_counts.player_death + 1
    local player = event:get_player()
    event_test_log(player, "player_death #" .. tostring(event_test_counts.player_death)
        .. " player=" .. player:get_name())
end)

hook.add_on_player_respawn(function(event)
    event_test_counts.player_respawn = event_test_counts.player_respawn + 1
    local player = event:get_player()
    event_test_log(player, "player_respawn #" .. tostring(event_test_counts.player_respawn)
        .. " player=" .. player:get_name())
end)

hook.add_on_item_consume(function(event)
    event_test_counts.item_consume = event_test_counts.item_consume + 1
    local player = event:get_player()
    local item = event:get_item()
    event_test_log(player, "item_consume #" .. tostring(event_test_counts.item_consume)
        .. " item=" .. item:get_display_name()
        .. " count=" .. tostring(item:get_count()))
end)

hook.add_on_chat(function(event)
    event_test_counts.chat = event_test_counts.chat + 1
    local player = event:get_player()
    event_test_log(player, "chat #" .. tostring(event_test_counts.chat)
        .. " player=" .. player:get_name()
        .. " message=" .. event:get_message())

    if event:get_message() == "aris-cancel-chat" then
        event:cancel()
        player:send_message_text("[event-test] chat cancelled")
    end
end)
