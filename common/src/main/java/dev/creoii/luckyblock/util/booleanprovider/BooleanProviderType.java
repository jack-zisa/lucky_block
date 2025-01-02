package dev.creoii.luckyblock.util.booleanprovider;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.LuckyBlockRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public record BooleanProviderType<P extends BooleanProvider>(MapCodec<P> codec) {
    public static final BooleanProviderType<SimpleBooleanProvider> SIMPLE_BOOLEAN_PROVIDER = new BooleanProviderType<>(SimpleBooleanProvider.CODEC);
    public static final BooleanProviderType<RandomBooleanProvider> RANDOM_BOOLEAN_PROVIDER = new BooleanProviderType<>(RandomBooleanProvider.CODEC);

    public static void init() {
        registerBooleanProviderType(Identifier.of(LuckyBlockMod.NAMESPACE, "simple_boolean_provider"), SIMPLE_BOOLEAN_PROVIDER);
        registerBooleanProviderType(Identifier.of(LuckyBlockMod.NAMESPACE, "random_boolean_provider"), RANDOM_BOOLEAN_PROVIDER);
    }

    private static void registerBooleanProviderType(Identifier id, BooleanProviderType<?> providerType) {
        Registry.register(LuckyBlockRegistries.BOOLEAN_PROVIDER_TYPE, id, providerType);
    }
}
