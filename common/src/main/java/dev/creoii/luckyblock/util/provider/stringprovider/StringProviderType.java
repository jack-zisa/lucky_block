package dev.creoii.luckyblock.util.provider.stringprovider;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.LuckyBlockMod;
import net.minecraft.registry.Registry;

public interface StringProviderType<P extends StringProvider> {
    StringProviderType<EmptyStringProvider> EMPTY = register("empty", EmptyStringProvider.CODEC);
    StringProviderType<ConstantStringProvider> CONSTANT = register("constant", ConstantStringProvider.CODEC);
    StringProviderType<RandomStringProvider> RANDOM = register("random", RandomStringProvider.CODEC);
    StringProviderType<WorldStringProvider> WORLD = register("world", WorldStringProvider.CODEC);

    MapCodec<P> codec();

    static <P extends StringProvider> StringProviderType<P> register(String id, MapCodec<P> mapCodec) {
        return Registry.register(LuckyBlockMod.STRING_PROVIDER_TYPES, id, () -> mapCodec);
    }
}
