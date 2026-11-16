package dev.creoii.luckyblock.util.provider.intprovider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;

public record AbsIntProvider(IntProvider value) implements IntProvider {
    public static final MapCodec<AbsIntProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(IntProviders.CODEC.fieldOf("value").forGetter(outcome -> outcome.value)
        ).apply(instance, AbsIntProvider::new);
    });

    public AbsIntProvider(IntProvider value) {
        this.value = value;
    }

    @Override
    public int sample(RandomSource random) {
        return Math.abs(value.sample(random));
    }

    @Override
    public int minInclusive() {
        return 0;
    }

    @Override
    public int maxInclusive() {
        return value.maxInclusive();
    }

    @Override
    public MapCodec<? extends IntProvider> codec() {
        return CODEC;
    }
}
