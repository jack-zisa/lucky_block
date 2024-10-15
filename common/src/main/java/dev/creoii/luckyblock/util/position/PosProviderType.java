package dev.creoii.luckyblock.util.position;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.LuckyBlockMod;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

@FunctionalInterface
public interface PosProviderType<T extends VecProvider> {
    PosProviderType<ConstantVecProvider> CONSTANT = () -> ConstantVecProvider.CODEC;
    PosProviderType<RandomInShapeVecProvider> RANDOM_IN_SHAPE = () -> RandomInShapeVecProvider.CODEC;

    MapCodec<T> codec();

    static void init() {
        register(new Identifier(LuckyBlockMod.NAMESPACE, "constant"), CONSTANT);
        register(new Identifier(LuckyBlockMod.NAMESPACE, "random_in_shape"), RANDOM_IN_SHAPE);
    }

    static void register(Identifier id, PosProviderType<?> type) {
        Registry.register(LuckyBlockMod.POS_PROVIDER_TYPES, id, type);
    }
}
