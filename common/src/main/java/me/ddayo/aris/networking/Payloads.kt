package me.ddayo.aris.networking

import me.ddayo.aris.RegistryHelper
import me.ddayo.aris.engine.networking.C2SPacketHandler
import me.ddayo.aris.engine.networking.PacketDeclaration
import me.ddayo.aris.engine.networking.S2CPacketHandler
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import org.apache.logging.log4j.LogManager

object NetworkPayloadManager {
    fun init() {
        NetworkingExtensions._registerPlayC2S(C2SLuaPayload.TYPE, C2SLuaPayload.CODEC)
        NetworkingExtensions._registerPlayS2C(S2CLuaPayload.TYPE, S2CLuaPayload.CODEC)

        NetworkingExtensions._registerPlayS2C(SyncDataPayload.ID, SyncDataPayload.CODEC)
        NetworkingExtensions._registerPlayS2C(ReloadEnginePayload.ID, ReloadEnginePayload.CODEC)
    }
}

class C2SLuaPayload(
    val id: ResourceLocation? = null,
    val dataToSend: PacketDeclaration.Builder? = null,
    val parsedData: Array<Pair<ResourceLocation, Any?>>? = null
) : CustomPacketPayload {

    override fun type() = TYPE

    companion object {
        val TYPE = CustomPacketPayload.Type<C2SLuaPayload>(RegistryHelper.getResourceLocation("generic_c2s"))

        val CODEC: StreamCodec<RegistryFriendlyByteBuf, C2SLuaPayload> = StreamCodec.of(
            { buf, payload ->
                payload.dataToSend!!.build(buf)
            },
            { buf ->
                val id = buf.readResourceLocation()
                val packetDecl = C2SPacketHandler.packets[id]!!
                val data = packetDecl.parse(buf)
                C2SLuaPayload(id = id, parsedData = data)
            }
        )
    }
}

class S2CLuaPayload(
    val id: ResourceLocation? = null,
    val dataToSend: PacketDeclaration.Builder? = null,
    val parsedData: Array<Pair<ResourceLocation, Any?>>? = null
) : CustomPacketPayload {

    override fun type() = TYPE

    companion object {
        val TYPE = CustomPacketPayload.Type<S2CLuaPayload>(RegistryHelper.getResourceLocation("generic_s2c"))

        val CODEC: StreamCodec<RegistryFriendlyByteBuf, S2CLuaPayload> = StreamCodec.of(
            { buf, payload ->
                payload.dataToSend!!.build(buf)
            },
            { buf ->
                val id = buf.readResourceLocation()
                val packetDecl = S2CPacketHandler.packets[id]!!
                val data = packetDecl.parse(buf)
                S2CLuaPayload(id = id, parsedData = data)
            }
        )
    }
}

data class SyncDataPayload(
    val id: String,
    val type: Byte,
    val stringData: String,
    val numberData: Double,
    val itemData: ItemStack
) : CustomPacketPayload {
    companion object {
        val ID = CustomPacketPayload.Type<SyncDataPayload>(RegistryHelper.getResourceLocation("sync_data"))
        val CODEC: StreamCodec<RegistryFriendlyByteBuf, SyncDataPayload> = StreamCodec.composite(
            StreamCodec.of({ b, v -> b.writeUtf(v) }, { b -> b.readUtf() }), SyncDataPayload::id,
            StreamCodec.of({ b, v -> b.writeByte(v.toInt()) }, { b -> b.readByte() }), SyncDataPayload::type,
            StreamCodec.of({ b, v -> b.writeUtf(v) }, { b -> b.readUtf() }), SyncDataPayload::stringData,
            StreamCodec.of({ b, v -> b.writeDouble(v) }, { b -> b.readDouble() }), SyncDataPayload::numberData,
            ItemStack.STREAM_CODEC, SyncDataPayload::itemData,
            ::SyncDataPayload
        )
    }

    override fun type() = ID
}

object ReloadEnginePayload : CustomPacketPayload {
    val ID = CustomPacketPayload.Type<ReloadEnginePayload>(RegistryHelper.getResourceLocation("reload_engine"))
    val CODEC: StreamCodec<RegistryFriendlyByteBuf, ReloadEnginePayload> = StreamCodec.unit(this)

    override fun type() = ID
}