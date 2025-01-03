package dev.creoii.luckyblock.util.textprovider;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.LuckyBlockRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public record TextProviderType<P extends TextProvider>(MapCodec<P> codec) {
    public static final TextProviderType<SimpleTextProvider> SIMPLE_TEXT_PROVIDER = new TextProviderType<>(SimpleTextProvider.CODEC);
    public static final TextProviderType<RandomTextProvider> RANDOM_TEXT_PROVIDER = new TextProviderType<>(RandomTextProvider.CODEC);
    public static final TextProviderType<CompoundTextProvider> COMPOUND_TEXT_PROVIDER = new TextProviderType<>(CompoundTextProvider.CODEC);
    public static final TextProviderType<WorldTextProvider> WORLD_TEXT_PROVIDER = new TextProviderType<>(WorldTextProvider.CODEC);
    public static final TextProviderType<EntityTextProvider> ENTITY_TEXT_PROVIDER = new TextProviderType<>(EntityTextProvider.CODEC);

    public static void init() {
        registerItemStackProviderType(Identifier.of(LuckyBlockMod.NAMESPACE, "simple_text_provider"), SIMPLE_TEXT_PROVIDER);
        registerItemStackProviderType(Identifier.of(LuckyBlockMod.NAMESPACE, "random_text_provider"), RANDOM_TEXT_PROVIDER);
        registerItemStackProviderType(Identifier.of(LuckyBlockMod.NAMESPACE, "compound_text_provider"), COMPOUND_TEXT_PROVIDER);
        registerItemStackProviderType(Identifier.of(LuckyBlockMod.NAMESPACE, "world_text_provider"), WORLD_TEXT_PROVIDER);
        registerItemStackProviderType(Identifier.of(LuckyBlockMod.NAMESPACE, "entity_text_provider"), ENTITY_TEXT_PROVIDER);
    }

    private static void registerItemStackProviderType(Identifier id, TextProviderType<?> providerType) {
        Registry.register(LuckyBlockRegistries.TEXT_PROVIDER_TYPE, id, providerType);
    }
}
