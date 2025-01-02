package dev.creoii.luckyblock.util.entityprovider;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.LuckyBlockRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public record EntityProviderType<P extends EntityProvider>(MapCodec<P> codec) {
    public static final EntityProviderType<SimpleEntityProvider> SIMPLE_ENTITY_PROVIDER = new EntityProviderType<>(SimpleEntityProvider.CODEC);
    public static final EntityProviderType<RandomInRangeEntityProvider> RANDOM_IN_RANGE_ENTITY_PROVIDER = new EntityProviderType<>(RandomInRangeEntityProvider.CODEC);
    public static final EntityProviderType<RandomPlayerEntityProvider> RANDOM_PLAYER_ENTITY_PROVIDER = new EntityProviderType<>(RandomPlayerEntityProvider.CODEC);
    public static final EntityProviderType<SourceEntityProvider> SOURCE_ENTITY_PROVIDER = new EntityProviderType<>(SourceEntityProvider.CODEC);
    public static final EntityProviderType<EntityEntityProvider> ENTITY_ENTITY_PROVIDER = new EntityProviderType<>(EntityEntityProvider.CODEC);

    public static void init() {
        registerEntityProviderType(Identifier.of(LuckyBlockMod.NAMESPACE, "simple_entity_provider"), SIMPLE_ENTITY_PROVIDER);
        registerEntityProviderType(Identifier.of(LuckyBlockMod.NAMESPACE, "random_in_range_entity_provider"), RANDOM_IN_RANGE_ENTITY_PROVIDER);
        registerEntityProviderType(Identifier.of(LuckyBlockMod.NAMESPACE, "random_player_entity_provider"), RANDOM_PLAYER_ENTITY_PROVIDER);
        registerEntityProviderType(Identifier.of(LuckyBlockMod.NAMESPACE, "source_entity_provider"), SOURCE_ENTITY_PROVIDER);
        registerEntityProviderType(Identifier.of(LuckyBlockMod.NAMESPACE, "entity_entity_provider"), ENTITY_ENTITY_PROVIDER);
    }

    private static void registerEntityProviderType(Identifier id, EntityProviderType<?> providerType) {
        Registry.register(LuckyBlockRegistries.ENTITY_PROVIDER_TYPE, id, providerType);
    }
}
