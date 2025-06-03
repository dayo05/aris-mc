package me.ddayo.aris.engine


interface EngineInitializer<T> where T: MCBaseEngine {
    fun initLua(engine: T)
}