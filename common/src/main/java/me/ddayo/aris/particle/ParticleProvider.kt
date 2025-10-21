package me.ddayo.aris.particle

import me.ddayo.aris.engine.client.ClientInitEngine
import me.ddayo.aris.lua.glue.ClientInitGenerated
import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.luagen.LuaProperty
import me.ddayo.aris.luagen.LuaProvider
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.ParticleRenderType
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.particle.TextureSheetParticle
import net.minecraft.core.particles.SimpleParticleType

class CustomParticle(level: ClientLevel, x: Double, y: Double, z: Double, vx: Double, vy: Double, vz: Double, lifetime: Int, hasPhysics: Boolean, friction: Float, quadSize: Float): TextureSheetParticle(level, x, y, z, vx, vy, vz) {
    init {
        this.lifetime = lifetime
        this.hasPhysics = hasPhysics
        this.friction = friction
        this.quadSize = quadSize
    }
    override fun getRenderType() = ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT
}

@LuaProvider(ClientInitEngine.PROVIDER)
class ParticleInfo(
    @LuaProperty
    var lifetime: Int = 40,
    @LuaProperty(name = "has_physics")
    var hasPhysics: Boolean = true,
    @LuaProperty
    var friction: Float = 0.98f,
    @LuaProperty
    var quadSize: Float = 0.1f,
    @LuaProperty
    var r: Float = 1.0f,
    @LuaProperty
    var g: Float = 1.0f,
    @LuaProperty
    var b: Float = 1.0f,
): ILuaStaticDecl by ClientInitGenerated.ParticleInfo_LuaGenerated

class CustomParticleProvider(
    private val sprites: SpriteSet,
    private val info: ParticleInfo
): ParticleProvider<SimpleParticleType> {
    override fun createParticle(
        particleOptions: SimpleParticleType,
        clientLevel: ClientLevel,
        x: Double,
        y: Double,
        z: Double,
        vx: Double,
        vy: Double,
        vz: Double
    ) = CustomParticle(clientLevel, x, y, z, vx, vy, vz, info.lifetime, info.hasPhysics, info.friction, info.quadSize).apply {
        pickSprite(sprites)
        setColor(info.r, info.g, info.b)
    }
}