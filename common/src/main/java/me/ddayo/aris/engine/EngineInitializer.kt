package me.ddayo.aris.engine


fun interface EngineInitializer<T> where T: MCBaseEngine {
    fun initLua(engine: T)
}