package dev.creoii.luckyblock.util.provider.intprovider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.IntProviderType;
import net.minecraft.util.math.random.Random;

public class AbsIntProvider extends IntProvider {
    public static final MapCodec<AbsIntProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(IntProvider.VALUE_CODEC.fieldOf("value").forGetter(outcome -> outcome.value)
        ).apply(instance, AbsIntProvider::new);
    });
    public final IntProvider value;

    public AbsIntProvider(IntProvider value) {
        this.value = value;
    }

    @Override
    public int get(Random random) {
        return Math.abs(value.get(random));
    }

    @Override
    public int getMin() {
        return value.getMin();
    }

    @Override
    public int getMax() {
        return value.getMax();
    }

    @Override
    public IntProviderType<?> getType() {
        return LuckyIntProviderTypes.ABS;
    }
}
