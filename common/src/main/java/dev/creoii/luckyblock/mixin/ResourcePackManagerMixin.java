package dev.creoii.luckyblock.mixin;

import dev.creoii.luckyblock.util.resource.LuckyBlockAddonsResourcePackCreator;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedHashSet;
import java.util.Set;

@Mixin(PackRepository.class)
public class ResourcePackManagerMixin {
    @Shadow
    @Final
    private Set<RepositorySource> sources;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void lucky$addBuiltInDataPacks(ResourcePackProvider[] resourcePackProviders, CallbackInfo info) {
        sources = new LinkedHashSet<>(sources);

        if (sources.stream().anyMatch(resourcePackProvider -> resourcePackProvider instanceof FileResourcePackProvider fileResourcePackProvider && (fileResourcePackProvider.source == ResourcePackSource.WORLD || fileResourcePackProvider.source == ResourcePackSource.SERVER))) {
            sources.add(new LuckyBlockAddonsResourcePackCreator(ResourceType.SERVER_DATA));
        }
    }
}