package me.ddayo.aris.client

import me.ddayo.aris.engine.client.ClientInGameEngine
import me.ddayo.aris.engine.client.ClientInitEngine
import me.ddayo.aris.engine.hook.client.ClientInGameHooks
import me.ddayo.aris.engine.client.ClientMainEngine
import me.ddayo.aris.networking.NetworkingExtensions
import net.minecraft.client.Minecraft
import party.iroiro.luajava.luajit.LuaJit
import java.util.function.Supplier

object ArisClient {
    private var wasAttackDown = false
    @Volatile
    private var clientMainStartRequested = false
    @Volatile
    private var clientMainStarted = false

    inline fun runOnClientThreadBlocking(crossinline block: () -> Unit) {
        val mc = Minecraft.getInstance()
        if (mc.isSameThread) {
            block()
            return
        }
        mc.submit(Supplier {
            block()
            Unit
        }).get()
    }

    fun init() {
        val engine = ClientInitEngine(LuaJit())

        while(engine.tasks.isNotEmpty()) {
            engine.loop()
            engine.removeAllFinished()
        }
        // The client init engine's scripting phase is over once initialization
        // finishes (only its collected data, e.g. particleInfo, is used later).
        engine.fireDisposeCallbacks()
    }

    fun clientTick() {
        val mc = Minecraft.getInstance()
        if (clientMainStartRequested && !clientMainStarted && mc.overlay == null) {
            ClientMainEngine.createEngine(LuaJit())
            clientMainStarted = true
        }
        ClientMainEngine.INSTANCE?.loop()
        detectLeftClick()
    }

    fun clientWorldTick() {
        ClientInGameEngine.INSTANCE?.tick()
    }

    fun onClientJoinGame() {
        runOnClientThreadBlocking {
            ClientInGameEngine.createEngine(LuaJit())
        }
    }

    fun onClientLeaveGame() {
        // The disconnect event may be dispatched off the client render thread;
        // LuaJIT is not thread-safe, so the leave hook and engine disposal must
        // run together on the render thread (and in that order).
        runOnClientThreadBlocking {
            ClientInGameEngine.INSTANCE?.let { ClientInGameHooks.executeOnPlayerLeave() }
            ClientInGameEngine.disposeEngine()
            wasAttackDown = false
        }
    }

    fun onClientClose() {
        runOnClientThreadBlocking {
            ClientMainEngine.disposeEngine()
            clientMainStarted = false
            clientMainStartRequested = false
        }
    }

    fun onClientStart() {
        clientMainStartRequested = true
    }

    private fun detectLeftClick() {
        val mc = Minecraft.getInstance()
        val attackDown = mc.options.keyAttack.isDown
        if (mc.player != null && mc.level != null && mc.screen == null && attackDown && !wasAttackDown) {
            NetworkingExtensions._sendLeftClickPacket()
        }
        wasAttackDown = attackDown
    }

    fun reloadEngine() {
        runOnClientThreadBlocking {
            if (Minecraft.getInstance().overlay != null) return@runOnClientThreadBlocking
            ClientMainEngine.INSTANCE ?: return@runOnClientThreadBlocking
            ClientMainEngine.disposeEngine()
            ClientMainEngine.createEngine(LuaJit())
            clientMainStarted = true
            ClientInGameEngine.INSTANCE?.run {
                // The local player "leaves" the outgoing engine; the matching
                // "join" fires automatically on the new engine's first tick().
                ClientInGameHooks.executeOnPlayerLeave()
                ClientInGameEngine.disposeEngine()
                ClientInGameEngine.createEngine(LuaJit())
            }
        }
    }
}
