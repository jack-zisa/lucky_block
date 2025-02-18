package dev.creoii.luckyblock.util.provider.integer;

import dev.creoii.luckyblock.LuckyBlockMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.IntProviderType;

public class LuckyIntProviderTypes {
    public static final IntProviderType<WorldIntProvider> WORLD = () -> WorldIntProvider.CODEC;

    public static void register() {
        Registry.register(Registries.INT_PROVIDER_TYPE, Identifier.of(LuckyBlockMod.NAMESPACE, "world"), WORLD);
    }
}
