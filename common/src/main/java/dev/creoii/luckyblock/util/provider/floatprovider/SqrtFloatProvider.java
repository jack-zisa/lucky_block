package dev.creoii.luckyblock.util.provider.floatprovider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.floatprovider.FloatProviderType;
import net.minecraft.util.math.random.Random;

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
    public float get(Random random) {
        return (float) Math.sqrt(value.get(random));
    }

    @Override
    public float getMin() {
        return value.getMin();
    }

    @Override
    public float getMax() {
        return value.getMax();
    }

    @Override
    public FloatProviderType<?> getType() {
        return FloatProviderType.SIN;
    }
}
