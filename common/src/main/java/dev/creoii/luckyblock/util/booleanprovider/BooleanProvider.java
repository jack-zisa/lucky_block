package dev.creoii.luckyblock.util.booleanprovider;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.creoii.luckyblock.LuckyBlockRegistries;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.util.math.random.Random;

import java.util.function.Function;

public abstract class BooleanProvider {
    public static final SimpleBooleanProvider TRUE = BooleanProvider.of(true);
    public static final SimpleBooleanProvider FALSE = BooleanProvider.of(false);
    public static final Codec<BooleanProvider> TYPE_CODEC = LuckyBlockRegistries.BOOLEAN_PROVIDER_TYPE.getCodec().dispatch(BooleanProvider::getType, BooleanProviderType::codec);
    public static final Codec<BooleanProvider> VALUE_CODEC = Codec.either(Codec.BOOL, TYPE_CODEC).xmap(either -> {
        return either.map(BooleanProvider::of, Function.identity());
    }, Either::right);

    public static SimpleBooleanProvider of(boolean value) {
        return new SimpleBooleanProvider(value);
    }

    protected abstract BooleanProviderType<?> getType();

    public abstract boolean getBoolean(Outcome.Context<?> context, Random random);
}