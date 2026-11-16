package dev.creoii.luckyblock.util.provider.stringprovider;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.creoii.luckyblock.LuckyBlockMod;
import net.minecraft.util.RandomSource;

import java.util.function.Function;

public abstract class StringProvider {
    public static final Codec<StringProvider> TYPE_CODEC = LuckyBlockMod.STRING_PROVIDER_TYPES.byNameCodec().dispatch(StringProvider::getType, StringProviderType::codec);
    public static final Codec<StringProvider> CODEC = Codec.either(Codec.STRING, TYPE_CODEC).xmap(either -> {
        return either.map(ConstantStringProvider::new, Function.identity());
    }, Either::right);

    public abstract String get(RandomSource random);

    public abstract StringProviderType<?> getType();
}
