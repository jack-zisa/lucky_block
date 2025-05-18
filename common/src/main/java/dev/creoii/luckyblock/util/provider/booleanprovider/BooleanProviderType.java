package dev.creoii.luckyblock.util.provider.booleanprovider;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.LuckyBlockMod;
import net.minecraft.registry.Registry;

public interface BooleanProviderType<P extends BooleanProvider> {
    BooleanProviderType<ConstantBooleanProvider> CONSTANT = register("constant", ConstantBooleanProvider.CODEC);
    BooleanProviderType<TrueBooleanProvider> TRUE = register("true", TrueBooleanProvider.CODEC);
    BooleanProviderType<FalseBooleanProvider> FALSE = register("false", FalseBooleanProvider.CODEC);
    BooleanProviderType<RandomBooleanProvider> RANDOM = register("random", RandomBooleanProvider.CODEC);
    BooleanProviderType<NotBooleanProvider> NOT = register("not", NotBooleanProvider.CODEC);
    BooleanProviderType<AndBooleanProvider> AND = register("and", AndBooleanProvider.CODEC);
    BooleanProviderType<OrBooleanProvider> OR = register("or", OrBooleanProvider.CODEC);
    BooleanProviderType<XorBooleanProvider> XOR = register("xor", XorBooleanProvider.CODEC);
    BooleanProviderType<WorldBooleanProvider> WORLD = register("world", WorldBooleanProvider.CODEC);
    MapCodec<P> codec();

    static <P extends BooleanProvider> BooleanProviderType<P> register(String id, MapCodec<P> mapCodec) {
        return Registry.register(LuckyBlockMod.BOOLEAN_PROVIDER_TYPES, id, () -> mapCodec);
    }
}
