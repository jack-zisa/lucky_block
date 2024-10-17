package dev.creoii.luckyblock.util.position;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.LuckyBlockMod;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

@FunctionalInterface
public interface VecProviderType<T extends VecProvider> {
    VecProviderType<ConstantVecProvider> CONSTANT = () -> ConstantVecProvider.CODEC;
    VecProviderType<RandomInShapeVecProvider> RANDOM_IN_SHAPE = () -> RandomInShapeVecProvider.CODEC;
    VecProviderType<RandomVelocityVecProvider> RANDOM_VELOCITY = () -> RandomVelocityVecProvider.CODEC;
    VecProviderType<HeightmapVecProvider> HEIGHTMAP = () -> HeightmapVecProvider.CODEC;

    MapCodec<T> codec();

    static void init() {
        register(new Identifier(LuckyBlockMod.NAMESPACE, "constant"), CONSTANT);
        register(new Identifier(LuckyBlockMod.NAMESPACE, "random_in_shape"), RANDOM_IN_SHAPE);
        register(new Identifier(LuckyBlockMod.NAMESPACE, "random_velocity"), RANDOM_VELOCITY);
        register(new Identifier(LuckyBlockMod.NAMESPACE, "heightmap"), HEIGHTMAP);
    }

    static void register(Identifier id, VecProviderType<?> type) {
        Registry.register(LuckyBlockMod.POS_PROVIDER_TYPES, id, type);
    }
}
