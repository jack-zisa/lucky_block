package dev.creoii.luckyblock.mixin;

import dev.creoii.luckyblock.util.resource.LuckyBlockAddonsResourcePackCreator;
import net.minecraft.resource.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedHashSet;
import java.util.Set;

@Mixin(ResourcePackManager.class)
public class ResourcePackManagerMixin {
    @Mutable @Shadow @Final private Set<ResourcePackProvider> providers;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void lucky$addBuiltInDataPacks(ResourcePackProvider[] resourcePackProviders, CallbackInfo info) {
        providers = new LinkedHashSet<>(providers);
        boolean shouldAddServerProvider = false;

        for (ResourcePackProvider provider : providers) {
            if (provider instanceof FileResourcePackProvider fileResourcePackProvider && (fileResourcePackProvider.source == ResourcePackSource.WORLD || fileResourcePackProvider.source == ResourcePackSource.SERVER)) {
                shouldAddServerProvider = true;
                break;
            }
        }

        if (shouldAddServerProvider) {
            providers.add(new LuckyBlockAddonsResourcePackCreator(ResourceType.SERVER_DATA));
        }
    }
}
