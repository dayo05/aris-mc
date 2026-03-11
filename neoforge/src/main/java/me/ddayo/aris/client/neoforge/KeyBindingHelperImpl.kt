package me.ddayo.aris.client.neoforge

import me.ddayo.aris.Aris
import net.minecraft.client.KeyMapping
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent

@EventBusSubscriber(modid = Aris.MOD_ID, value = [Dist.CLIENT])
object KeyBindingHelperImpl {
    private val bindings = mutableListOf<KeyMapping>()

    @JvmStatic
    fun registerPlatform(binding: KeyMapping): KeyMapping {
        bindings.add(binding)
        return binding
    }

    @SubscribeEvent
    fun onRegisterKeyMappings(event: RegisterKeyMappingsEvent) {
        for (binding in bindings) {
            event.register(binding)
        }
    }
}
