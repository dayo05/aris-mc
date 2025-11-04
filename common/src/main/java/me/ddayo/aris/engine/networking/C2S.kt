package me.ddayo.aris.engine.networking

import dev.architectury.injectables.annotations.ExpectPlatform
import me.ddayo.aris.Aris
import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.luagen.LuaFunc
import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.engine.InitEngine
import me.ddayo.aris.engine.client.ClientInGameEngine
import me.ddayo.aris.engine.hook.ServerNetworkingHooks
import me.ddayo.aris.engine.wrapper.LuaServerPlayer
import me.ddayo.aris.lua.glue.InitGenerated
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import org.apache.logging.log4j.LogManager


@LuaProvider(InitEngine.PROVIDER)
class C2SPacketDeclaration(id: ResourceLocation) : PacketDeclaration(id), ILuaStaticDecl by InitGenerated.C2SPacketDeclaration_LuaGenerated {
    override fun parse(buf: FriendlyByteBuf): Array<Pair<ResourceLocation, Any?>> {
        return frozen.map { it.first to it.second.process(buf) }.toTypedArray()
    }

    override fun getFunction() = ServerNetworkingHooks.packetHooks[id]

    fun execute(player: ServerPlayer, parsed: Array<Pair<ResourceLocation, Any?>>) {
        getFunction().callAsTaskRawArg { task ->
            engine.luaMain.pushNoInline(task.coroutine, LuaServerPlayer(player))
            task.coroutine.newTable()
            for((rl, act) in parsed)
                if(engine.luaMain.pushNoInline(task.coroutine, act) == 1)
                    task.coroutine.setField(-2, rl.path)
            2
        }
    }
}

@LuaProvider(InitEngine.PROVIDER, library = "aris.init.networking")
object C2SPacketHandler {
    val packets = mutableMapOf<ResourceLocation, C2SPacketDeclaration>()

    /**
     * 패킷을 새로 생성합니다.
     * @param _id 패킷 id
     * @return 생성된 패킷 정의 Builder
     */
    @LuaFunction("create_c2s_packet")
    fun createC2SPacket(_id: String): C2SPacketDeclaration {
        val id = ResourceLocation(Aris.MOD_ID, _id)
        val packet = C2SPacketDeclaration(id)
        packets[id] = packet
        return packet
    }
}