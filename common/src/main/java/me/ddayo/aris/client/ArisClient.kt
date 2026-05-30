package me.ddayo.aris.client

import me.ddayo.aris.engine.client.ClientInGameEngine
import me.ddayo.aris.engine.client.ClientInitEngine
import me.ddayo.aris.engine.hook.client.ClientInGameHooks
import me.ddayo.aris.engine.client.ClientMainEngine
import net.minecraft.client.Minecraft
import party.iroiro.luajava.luajit.LuaJit
import java.util.function.Supplier

object ArisClient {
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
        ClientMainEngine.INSTANCE?.loop()
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
        }
    }

    fun onClientClose() {
        runOnClientThreadBlocking {
            ClientMainEngine.disposeEngine()
        }
    }

    fun onClientStart() {
        runOnClientThreadBlocking {
            ClientMainEngine.createEngine(LuaJit())
        }
    }

    fun reloadEngine() {
        runOnClientThreadBlocking {
            ClientMainEngine.INSTANCE ?: return@runOnClientThreadBlocking
            ClientMainEngine.disposeEngine()
            ClientMainEngine.createEngine(LuaJit())
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
