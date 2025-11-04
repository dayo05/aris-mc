package me.ddayo.aris.engine.networking

import dev.architectury.injectables.annotations.ExpectPlatform
import me.ddayo.aris.Aris
import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.luagen.LuaFunc
import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.engine.InitEngine
import me.ddayo.aris.engine.client.ClientInGameEngine
import me.ddayo.aris.engine.hook.client.ClientNetworkingHooks
import me.ddayo.aris.engine.wrapper.LuaServerPlayer
import me.ddayo.aris.lua.glue.InitGenerated
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import org.apache.logging.log4j.LogManager


@LuaProvider(InitEngine.PROVIDER)
class S2CPacketDeclaration(id: ResourceLocation) : PacketDeclaration(id), ILuaStaticDecl by InitGenerated.S2CPacketDeclaration_LuaGenerated {
    override fun parse(buf: FriendlyByteBuf): Array<Pair<ResourceLocation, Any?>> {
        return frozen.map { it.first to it.second.process(buf) }.toTypedArray()
    }

    override fun getFunction() = ClientNetworkingHooks.packetHooks[id]

    fun execute(parsed: Array<Pair<ResourceLocation, Any?>>) {
        getFunction().callAsTaskRawArg { task ->
            task.coroutine.newTable()
            for((rl, act) in parsed)
                if(engine.luaMain.pushNoInline(task.coroutine, act) == 1)
                    task.coroutine.setField(-2, rl.path)
            1
        }
    }
}

@LuaProvider(InitEngine.PROVIDER, library = "aris.init.networking")
object S2CPacketHandler {
    val packets = mutableMapOf<ResourceLocation, S2CPacketDeclaration>()

    /**
     * 패킷을 새로 생성합니다.
     * @param _id 패킷 id
     * @return 생성된 패킷 정의 Builder
     */
    @LuaFunction("create_s2c_packet")
    fun createS2CPacket(_id: String): S2CPacketDeclaration {
        val id = ResourceLocation(Aris.MOD_ID, _id)
        val packet = S2CPacketDeclaration(id)
        packets[id] = packet
        return packet
    }
}