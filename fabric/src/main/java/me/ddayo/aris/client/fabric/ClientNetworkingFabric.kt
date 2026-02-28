package me.ddayo.aris.client.fabric

import me.ddayo.aris.client.ArisClient
import me.ddayo.aris.engine.client.ClientInGameEngine
import me.ddayo.aris.engine.networking.*
import me.ddayo.aris.fabric.ServerNetworkingFabric
import me.ddayo.aris.networking.ReloadEnginePayload
import me.ddayo.aris.networking.S2CLuaPayload
import me.ddayo.aris.networking.SyncDataPayload
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking

object ClientNetworkingFabric {
    fun register() {
        ClientPlayNetworking.registerGlobalReceiver(SyncDataPayload.ID) { payload, context ->
            val type = ServerNetworkingFabric.ScriptDataType.entries[payload.type.toInt()]
            context.client().execute {
                ClientInGameEngine.INSTANCE?.let { engine ->
                    when (type) {
                        ServerNetworkingFabric.ScriptDataType.STRING -> engine.clientStringData[payload.id] = payload.stringData
                        ServerNetworkingFabric.ScriptDataType.NUMBER -> engine.clientNumberData[payload.id] = payload.numberData
                        ServerNetworkingFabric.ScriptDataType.ITEM -> engine.clientItemStackData[payload.id] = payload.itemData
                    }
                }
            }
        }

        ClientPlayNetworking.registerGlobalReceiver(ReloadEnginePayload.ID) { payload, context ->
            context.client().execute {
                ArisClient.reloadEngine()
            }
        }

        ClientPlayNetworking.registerGlobalReceiver(S2CLuaPayload.TYPE) { payload, context ->
            context.client().execute {
                val packet = S2CPacketHandler.packets[payload.id]!!
                packet.execute(payload.parsedData!!)
            }
        }
    }
}