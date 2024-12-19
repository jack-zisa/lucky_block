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
    @Mutable @Shadow @Final
    public Set<ResourcePackProvider> providers;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void lucky$addBuiltInDataPacks(ResourcePackProvider[] resourcePackProviders, CallbackInfo info) {
        providers = new LinkedHashSet<>(providers);

        if (providers.stream().anyMatch(resourcePackProvider -> resourcePackProvider instanceof FileResourcePackProvider fileResourcePackProvider && (fileResourcePackProvider.source == ResourcePackSource.WORLD || fileResourcePackProvider.source == ResourcePackSource.SERVER))) {
            providers.add(new LuckyBlockAddonsResourcePackCreator(ResourceType.SERVER_DATA));
        }
    }
}
