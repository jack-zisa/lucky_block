package dev.creoii.luckyblock.util.provider.booleanprovider;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.creoii.luckyblock.LuckyBlockMod;
import net.minecraft.util.RandomSource;

import java.util.function.Function;

public abstract class BooleanProvider {
    public static final Codec<BooleanProvider> TYPE_CODEC = LuckyBlockMod.BOOLEAN_PROVIDER_TYPES.byNameCodec().dispatch(BooleanProvider::getType, BooleanProviderType::codec);
    public static final Codec<BooleanProvider> CODEC = Codec.either(Codec.BOOL, TYPE_CODEC).xmap(either -> {
        return either.map(ConstantBooleanProvider::new, Function.identity());
    }, Either::right);

    public abstract boolean get(RandomSource random);

    public abstract BooleanProviderType<?> getType();
}
