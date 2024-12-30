package dev.creoii.luckyblock.util.colorprovider;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.LuckyBlockRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public record ColorProviderType<P extends ColorProvider>(MapCodec<P> codec) {
    public static final ColorProviderType<SimpleColorProvider> SIMPLE_COLOR_PROVIDER = new ColorProviderType<>(SimpleColorProvider.CODEC);
    public static final ColorProviderType<WeightedColorProvider> WEIGHTED_COLOR_PROVIDER = new ColorProviderType<>(WeightedColorProvider.CODEC);
    public static final ColorProviderType<RandomColorProvider> RANDOM_COLOR_PROVIDER = new ColorProviderType<>(RandomColorProvider.CODEC);

    public static void init() {
        registerColorProviderType(Identifier.of(LuckyBlockMod.NAMESPACE, "simple_color_provider"), SIMPLE_COLOR_PROVIDER);
        registerColorProviderType(Identifier.of(LuckyBlockMod.NAMESPACE, "weighted_color_provider"), WEIGHTED_COLOR_PROVIDER);
        registerColorProviderType(Identifier.of(LuckyBlockMod.NAMESPACE, "random_color_provider"), RANDOM_COLOR_PROVIDER);
    }

    private static void registerColorProviderType(Identifier id, ColorProviderType<?> providerType) {
        Registry.register(LuckyBlockRegistries.COLOR_PROVIDER_TYPE, id, providerType);
    }
}
