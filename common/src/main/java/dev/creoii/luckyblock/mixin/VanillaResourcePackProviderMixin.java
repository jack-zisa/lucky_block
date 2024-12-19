package dev.creoii.luckyblock.mixin;

import dev.creoii.luckyblock.util.resource.LuckyBlockAddonsResourcePackCreator;
import net.minecraft.client.resource.DefaultClientResourcePackProvider;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.VanillaResourcePackProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(VanillaResourcePackProvider.class)
public class VanillaResourcePackProviderMixin {
    @Inject(method = "register", at = @At("RETURN"))
    private void lucky$addBuiltInResourcePacks(Consumer<ResourcePackProfile> consumer, CallbackInfo ci) {
        if ((Object) this instanceof DefaultClientResourcePackProvider) {
            LuckyBlockAddonsResourcePackCreator.CLIENT_INSTANCE.register(consumer);
        }
    }
}