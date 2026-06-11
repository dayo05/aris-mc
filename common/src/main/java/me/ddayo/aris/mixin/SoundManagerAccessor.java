package me.ddayo.aris.mixin;

import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(SoundManager.class)
public interface SoundManagerAccessor {
    @Accessor("registry")
    Map<ResourceLocation, WeighedSoundEvents> aris$getRegistry();

    @Accessor("soundCache")
    Map<ResourceLocation, Resource> aris$getSoundCache();
}
