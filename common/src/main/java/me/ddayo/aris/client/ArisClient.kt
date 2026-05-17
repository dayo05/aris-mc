package me.ddayo.aris.client

import me.ddayo.aris.engine.client.ClientInGameEngine
import me.ddayo.aris.engine.client.ClientInitEngine
import me.ddayo.aris.engine.hook.client.ClientInGameHooks
import me.ddayo.aris.engine.client.ClientMainEngine
import net.minecraft.client.Minecraft
import party.iroiro.luajava.luajit.LuaJit

object ArisClient {
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
        ClientInGameEngine.createEngine(LuaJit())
    }

    fun onClientLeaveGame() {
        // The disconnect event may be dispatched off the client render thread;
        // LuaJIT is not thread-safe, so the leave hook and engine disposal must
        // run together on the render thread (and in that order).
        val mc = Minecraft.getInstance()
        val task = Runnable {
            ClientInGameEngine.INSTANCE?.let { ClientInGameHooks.executeOnPlayerLeave() }
            ClientInGameEngine.disposeEngine()
        }
        if (mc.isSameThread) task.run() else mc.execute(task)
    }

    fun onClientClose() {
        ClientMainEngine.disposeEngine()
    }

    fun onClientStart() {
        ClientMainEngine.createEngine(LuaJit())
    }

    fun reloadEngine() {
        ClientMainEngine.INSTANCE?.run {
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