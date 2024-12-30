package dev.creoii.luckyblock.util.stackprovider;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.LuckyBlockRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public record ItemStackProviderType<P extends ItemStackProvider>(MapCodec<P> codec) {
    public static final ItemStackProviderType<SimpleItemStackProvider> SIMPLE_STACK_PROVIDER = new ItemStackProviderType<>(SimpleItemStackProvider.CODEC);
    public static final ItemStackProviderType<WeightedItemStackProvider> WEIGHTED_STACK_PROVIDER = new ItemStackProviderType<>(WeightedItemStackProvider.CODEC);

    public static void init() {
        registerItemStackProviderType(Identifier.of(LuckyBlockMod.NAMESPACE, "simple_stack_provider"), SIMPLE_STACK_PROVIDER);
        registerItemStackProviderType(Identifier.of(LuckyBlockMod.NAMESPACE, "weighted_stack_provider"), WEIGHTED_STACK_PROVIDER);
    }

    private static void registerItemStackProviderType(Identifier id, ItemStackProviderType<?> providerType) {
        Registry.register(LuckyBlockRegistries.ITEM_STACK_PROVIDER_TYPE, id, providerType);
    }
}
