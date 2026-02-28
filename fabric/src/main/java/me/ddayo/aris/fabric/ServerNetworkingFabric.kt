package me.ddayo.aris.fabric

import me.ddayo.aris.engine.networking.C2SPacketHandler
import me.ddayo.aris.networking.C2SLuaPayload
import me.ddayo.aris.networking.ReloadEnginePayload
import me.ddayo.aris.networking.SyncDataPayload
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack

object ServerNetworkingFabric {
    enum class ScriptDataType {
        STRING, NUMBER, ITEM
    }

    fun sendReloadPacketFabricLike(player: ServerPlayer) {
        ServerPlayNetworking.send(player, ReloadEnginePayload)
    }

    fun sendDataPacketFabricLike(player: ServerPlayer, of: String, data: Any) {
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
        ServerPlayNetworking.send(player, payload)
    }

    fun init() {
        ServerPlayNetworking.registerGlobalReceiver(C2SLuaPayload.TYPE) { payload, context ->
            context.server().execute {
                val packet = C2SPacketHandler.packets[payload.id]!!
                packet.execute(context.player(), payload.parsedData!!)
            }
        }
    }
}