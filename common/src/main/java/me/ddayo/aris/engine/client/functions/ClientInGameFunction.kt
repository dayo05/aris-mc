package me.ddayo.aris.engine.client.functions

import com.mojang.blaze3d.platform.InputConstants
import me.ddayo.aris.client.gui.HudRenderer
import me.ddayo.aris.engine.client.ClientInGameEngine
import me.ddayo.aris.engine.hook.client.ClientInGameHooks
import me.ddayo.aris.engine.wrapper.LuaEntity
import me.ddayo.aris.engine.wrapper.LuaItemStack
import me.ddayo.aris.luagen.LuaFunc
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import me.ddayo.aris.luagen.RetrieveEngine
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.projectile.ProjectileUtil
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.AABB
import org.apache.logging.log4j.LogManager


@LuaProvider(ClientInGameEngine.PROVIDER, library = "aris.game.client")
object ClientInGameFunction {
    private val mc by lazy { Minecraft.getInstance() }
    /**
     * 채팅창에 새로운 텍스트를 추가합니다.
     * @param message 추가할 메시지
     */
    @LuaFunction("send_system_message")
    fun sendSystemMessage(message: String) {
        mc.player!!.sendSystemMessage(Component.literal(message))
    }

    /**
     * 채팅창에 /command를 입력하는 것과 동일합니다.
     * @param command 실행할 커멘드
     */
    @LuaFunction("invoke_command")
    fun invokeCommand(command: String) {
        mc.player!!.connection.sendCommand(command)
    }

    /**
     * 현재 플레이어의 x좌표를 구합니다.
     */
    @LuaFunction("get_player_x")
    fun getPlayerX() = mc.player!!.x

    /**
     * 현재 플레이어의 y좌표를 구합니다.
     */
    @LuaFunction("get_player_y")
    fun getPlayerY() = mc.player!!.y

    @LuaFunction("get_player_z")
    fun getPlayerZ() = mc.player!!.z

    @LuaFunction("get_player_pitch")
    fun getPlayerPitch() = mc.player!!.xRot

    @LuaFunction("get_player_yaw")
    fun getPlayerYaw() = mc.player!!.yRot

    fun warnTickFunction() = LogManager.getLogger().warn("Use aris.game.client.hook.* instead.")
    /**
     * 매 틱마다 실행할 함수를 추가합니다.
     * @param f 실행할 함수
     */
    @LuaFunction("add_tick_hook")
    fun addTickHook(@RetrieveEngine engine: ClientInGameEngine, f: LuaFunc) {
        warnTickFunction()
        ClientInGameHooks.addTickHook(engine, f)
    }

    /**
     * 플레이어가 얼마나 오랫동안 아이템을 사용했는지(charging)
     * @return 플레이어가 차징한 시간(tick)
     */
    @LuaFunction("item_used_duration")
    fun getUsedDuration(): Int {
        val player = Minecraft.getInstance().player!!
        return player.useItem.useDuration - player.useItemRemainingTicks
    }

    /**
     * HUD를 생성합니다.
     */
    @LuaFunction("create_hud")
    fun createHud() = HudRenderer()

    /**
     * 모든 열려있는 HUD를 닫습니다.
     */
    @LuaFunction("clear_opened_hud")
    fun clearHud(@RetrieveEngine engine: ClientInGameEngine) = engine.enabledHud.clear()

    /**
     * 서버로부터 전송받은 문자열 데이터를 가져옵니다.
     */
    @LuaFunction("remote_string_data")
    fun getStringData(@RetrieveEngine engine: ClientInGameEngine, of: String) = engine.clientStringData[of] ?: "null"

    /**
     * 서버로부터 전송받은 정수 데이터를 가져옵니다.
     */
    @LuaFunction("remote_number_data")
    fun getNumberData(@RetrieveEngine engine: ClientInGameEngine, of: String) = engine.clientNumberData[of] ?: 0.0

    /**
     * 서버로부터 전송받은 아이템 데이터를 가져옵니다.
     */
    @LuaFunction("remove_item_data")
    fun getItemData(@RetrieveEngine engine: ClientInGameEngine, of: String) = LuaItemStack(engine.clientItemStackData[of] ?: ItemStack.EMPTY)

    /**
     * 새로 추가한 조작키를 실행할때 실행될 함수를 지정합니다.
     * @param key 누를 키
     * @param function 실행할 함수
     */
    @LuaFunction("add_on_key_pressed")
    fun onKeyPressed(@RetrieveEngine engine: ClientInGameEngine, key: String, function: LuaFunc) {
        warnTickFunction()
        ClientInGameHooks.onKeyPressed(key, function)
    }

    /**
     * 특정 키가 눌린 상태인지 검사합니다.
     * 이 함수는 씹힐 위험이 있으니, 사용을 지양하세요.
     * @param key 눌려져있는지 확인할 키
     */
    @LuaFunction("is_key_pressed")
    fun isKeyPressed(key: Int): Boolean {
        return InputConstants.isKeyDown(Minecraft.getInstance().window.window, key)
    }

    @LuaFunction("target_crosshair_entity")
    fun getCrosshairEntity(reach: Double): LuaEntity?
    {
        val player = Minecraft.getInstance().player!!
        val eyePos = player.getEyePosition(1.0f)
        val lookVec = player.getViewVector(1.0f)
        val endPos = eyePos.add(lookVec.scale(reach))

        val searchBox: AABB = player.boundingBox
            .expandTowards(lookVec.scale(reach))
            .inflate(1.0)

        return ProjectileUtil.getEntityHitResult(
            player,
            eyePos,
            endPos,
            searchBox,
            { entity: Entity? -> !entity!!.isSpectator && entity.isPickable },
            reach * reach
        )?.entity?.let { LuaEntity(it) }
    }
}