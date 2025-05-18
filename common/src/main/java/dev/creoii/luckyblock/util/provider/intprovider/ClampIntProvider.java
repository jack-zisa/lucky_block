package dev.creoii.luckyblock.util.provider.intprovider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.IntProviderType;
import net.minecraft.util.math.random.Random;

public class ClampIntProvider extends IntProvider {
    public static final MapCodec<ClampIntProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(IntProvider.VALUE_CODEC.fieldOf("value").forGetter(outcome -> outcome.value),
                IntProvider.VALUE_CODEC.fieldOf("min").forGetter(outcome -> outcome.min),
                IntProvider.VALUE_CODEC.fieldOf("max").forGetter(outcome -> outcome.max)
        ).apply(instance, ClampIntProvider::new);
    });
    public final IntProvider value;
    public final IntProvider min;
    public final IntProvider max;

    public ClampIntProvider(IntProvider value, IntProvider min, IntProvider max) {
        this.value = value;
        this.min = min;
        this.max = max;
    }

    @Override
    public int get(Random random) {
        return Math.clamp(value.get(random), min.get(random), max.get(random));
    }

    @Override
    public int getMin() {
        return min.getMin();
    }

    @Override
    public int getMax() {
        return max.getMax();
    }

    @Override
    public IntProviderType<?> getType() {
        return LuckyIntProviderTypes.CLAMP;
    }
}
