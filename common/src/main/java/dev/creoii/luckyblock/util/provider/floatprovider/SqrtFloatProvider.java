package dev.creoii.luckyblock.util.provider.floatprovider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;

public class SqrtFloatProvider extends FloatProvider {
    public static final MapCodec<SqrtFloatProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(FloatProvider.VALUE_CODEC.fieldOf("value").forGetter(outcome -> outcome.value)
        ).apply(instance, SqrtFloatProvider::new);
    });
    public final FloatProvider value;

    public SqrtFloatProvider(FloatProvider value) {
        this.value = value;
    }

    @Override
    public float sample(RandomSource random) {
        return (float) Math.sqrt(value.get(random));
    }

    @Override
    public float min() {
        return value.getMin();
    }

    @Override
    public float max() {
        return value.getMax();
    }

    @Override
    public FloatProviderType<?> getType() {
        return LuckyFloatProviderTypes.SIN;
    }
}
