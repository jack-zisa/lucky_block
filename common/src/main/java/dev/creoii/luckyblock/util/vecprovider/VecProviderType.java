package dev.creoii.luckyblock.util.vecprovider;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.LuckyBlockRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

@FunctionalInterface
public interface VecProviderType<T extends VecProvider> {
    VecProviderType<ConstantVecProvider> CONSTANT = () -> ConstantVecProvider.CODEC;
    VecProviderType<RandomInShapeVecProvider> RANDOM_IN_SHAPE = () -> RandomInShapeVecProvider.CODEC;
    VecProviderType<RandomVelocityVecProvider> RANDOM_VELOCITY = () -> RandomVelocityVecProvider.CODEC;
    VecProviderType<ClampToHeightmapVecProvider> CLAMP_TO_HEIGHTMAP = () -> ClampToHeightmapVecProvider.CODEC;
    VecProviderType<RandomVecProvider> RANDOM = () -> RandomVecProvider.CODEC;
    VecProviderType<EntityVecProvider> ENTITY = () -> EntityVecProvider.CODEC;
    VecProviderType<WorldVecProvider> WORLD = () -> WorldVecProvider.CODEC;

    MapCodec<T> codec();

    static void init() {
        register(Identifier.of(LuckyBlockMod.NAMESPACE, "constant"), CONSTANT);
        register(Identifier.of(LuckyBlockMod.NAMESPACE, "random_in_shape"), RANDOM_IN_SHAPE);
        register(Identifier.of(LuckyBlockMod.NAMESPACE, "random_velocity"), RANDOM_VELOCITY);
        register(Identifier.of(LuckyBlockMod.NAMESPACE, "clamp_to_heightmap"), CLAMP_TO_HEIGHTMAP);
        register(Identifier.of(LuckyBlockMod.NAMESPACE, "random"), RANDOM);
        register(Identifier.of(LuckyBlockMod.NAMESPACE, "entity"), ENTITY);
        register(Identifier.of(LuckyBlockMod.NAMESPACE, "world"), WORLD);
    }

    private static void register(Identifier id, VecProviderType<?> type) {
        Registry.register(LuckyBlockRegistries.POS_PROVIDER_TYPES, id, type);
    }
}
