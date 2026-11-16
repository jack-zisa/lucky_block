package dev.creoii.luckyblock.util.provider.intprovider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;

public record ModIntProvider(IntProvider a, IntProvider b) implements IntProvider {
    public static final MapCodec<ModIntProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(IntProviders.CODEC.fieldOf("a").forGetter(outcome -> outcome.a),
                IntProviders.CODEC.fieldOf("b").forGetter(outcome -> outcome.b)
        ).apply(instance, ModIntProvider::new);
    });

    @Override
    public int sample(RandomSource random) {
        return a.sample(random) % b.sample(random);
    }

    @Override
    public int minInclusive() {
        return a.minInclusive() + b.minInclusive();
    }

    @Override
    public int maxInclusive() {
        return a.maxInclusive() + b.maxInclusive();
    }

    @Override
    public MapCodec<? extends IntProvider> codec() {
        return CODEC;
    }
}
