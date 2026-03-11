package me.ddayo.aris.neoforge

import me.ddayo.aris.Aris
import me.ddayo.aris.client.ArisClient
import me.ddayo.aris.engine.client.ClientInGameEngine
import me.ddayo.aris.engine.networking.C2SPacketHandler
import me.ddayo.aris.engine.networking.S2CPacketHandler
import me.ddayo.aris.networking.C2SLuaPayload
import me.ddayo.aris.networking.ReloadEnginePayload
import me.ddayo.aris.networking.S2CLuaPayload
import me.ddayo.aris.networking.SyncDataPayload
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.IPayloadContext

@EventBusSubscriber(modid = Aris.MOD_ID)
object ArisNeoForgeNetworking {
    enum class ScriptDataType { STRING, NUMBER, ITEM }

    @SubscribeEvent
    @JvmStatic
    fun register(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar("1")

        // S2C payloads
        registrar.playToClient(SyncDataPayload.ID, SyncDataPayload.CODEC) { payload, context ->
            handleSyncData(payload, context)
        }
        registrar.playToClient(ReloadEnginePayload.ID, ReloadEnginePayload.CODEC) { _, context ->
            context.enqueueWork {
                ArisClient.reloadEngine()
            }
        }
        registrar.playToClient(S2CLuaPayload.TYPE, S2CLuaPayload.CODEC) { payload, context ->
            context.enqueueWork {
                val packet = S2CPacketHandler.packets[payload.id]!!
                packet.execute(payload.parsedData!!)
            }
        }

        // C2S payloads
        registrar.playToServer(C2SLuaPayload.TYPE, C2SLuaPayload.CODEC) { payload, context ->
            context.enqueueWork {
                val player = context.player() as ServerPlayer
                val packet = C2SPacketHandler.packets[payload.id]!!
                packet.execute(player, payload.parsedData!!)
            }
        }
    }

    private fun handleSyncData(payload: SyncDataPayload, context: IPayloadContext) {
        val type = ScriptDataType.entries[payload.type.toInt()]
        context.enqueueWork {
            ClientInGameEngine.INSTANCE?.let { engine ->
                when (type) {
                    ScriptDataType.STRING -> engine.clientStringData[payload.id] = payload.stringData
                    ScriptDataType.NUMBER -> engine.clientNumberData[payload.id] = payload.numberData
                    ScriptDataType.ITEM -> engine.clientItemStackData[payload.id] = payload.itemData
                }
            }
        }
    }

    fun sendDataPacketNeoForge(player: ServerPlayer, of: String, data: Any) {
        val payload = when (data) {
            is String -> SyncDataPayload(
                id = of,
                type = ScriptDataType.STRING.ordinal.toByte(),
                stringData = data,
                numberData = 0.0,
                itemData = ItemStack.EMPTY
            )
            is Number -> SyncDataPayload(
                id = of,
                type = ScriptDataType.NUMBER.ordinal.toByte(),
                stringData = "",
                numberData = data.toDouble(),
                itemData = ItemStack.EMPTY
            )
            is ItemStack -> SyncDataPayload(
                id = of,
                type = ScriptDataType.ITEM.ordinal.toByte(),
                stringData = "",
                numberData = 0.0,
                itemData = data
            )
            else -> return
        }
        PacketDistributor.sendToPlayer(player, payload)
    }

    fun sendReloadPacketNeoForge(player: ServerPlayer) {
        PacketDistributor.sendToPlayer(player, ReloadEnginePayload)
    }
}
