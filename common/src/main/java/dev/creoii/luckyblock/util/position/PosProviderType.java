package dev.creoii.luckyblock.util.position;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.LuckyBlockMod;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

@FunctionalInterface
public interface PosProviderType<T extends PosProvider> {
    PosProviderType<ConstantPosProvider> CONSTANT = () -> ConstantPosProvider.CODEC;
    PosProviderType<InShapePosProvider> IN_SHAPE = () -> InShapePosProvider.CODEC;

    MapCodec<T> codec();

    static void init() {
        register(new Identifier(LuckyBlockMod.NAMESPACE, "constant"), CONSTANT);
        register(new Identifier(LuckyBlockMod.NAMESPACE, "in_shape"), IN_SHAPE);
    }

    static void register(Identifier id, PosProviderType<?> type) {
        Registry.register(LuckyBlockMod.POS_PROVIDER_TYPES, id, type);
    }
}
