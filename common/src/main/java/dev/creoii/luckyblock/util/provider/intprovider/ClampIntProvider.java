package dev.creoii.luckyblock.util.provider.intprovider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;

public record ClampIntProvider(IntProvider value, IntProvider min, IntProvider max) implements IntProvider {
    public static final MapCodec<ClampIntProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(IntProviders.CODEC.fieldOf("value").forGetter(outcome -> outcome.value),
                IntProviders.CODEC.fieldOf("min").forGetter(outcome -> outcome.min),
                IntProviders.CODEC.fieldOf("max").forGetter(outcome -> outcome.max)
        ).apply(instance, ClampIntProvider::new);
    });

    @Override
    public int sample(RandomSource random) {
        return Math.clamp(value.sample(random), min.sample(random), max.sample(random));
    }

    @Override
    public int minInclusive() {
        return min.minInclusive();
    }

    @Override
    public int maxInclusive() {
        return max.maxInclusive();
    }

    @Override
    public MapCodec<? extends IntProvider> codec() {
        return CODEC;
    }
}
