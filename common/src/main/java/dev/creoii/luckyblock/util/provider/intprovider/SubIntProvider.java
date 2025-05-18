package dev.creoii.luckyblock.util.provider.intprovider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.IntProviderType;
import net.minecraft.util.math.random.Random;

public class SubIntProvider extends IntProvider {
    public static final MapCodec<SubIntProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(IntProvider.VALUE_CODEC.fieldOf("a").forGetter(outcome -> outcome.a),
                IntProvider.VALUE_CODEC.fieldOf("b").forGetter(outcome -> outcome.b)
        ).apply(instance, SubIntProvider::new);
    });
    public final IntProvider a;
    public final IntProvider b;

    public SubIntProvider(IntProvider a, IntProvider b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public int get(Random random) {
        return a.get(random) - b.get(random);
    }

    @Override
    public int getMin() {
        return a.getMin() + b.getMin();
    }

    @Override
    public int getMax() {
        return a.getMax() + b.getMax();
    }

    @Override
    public IntProviderType<?> getType() {
        return LuckyIntProviderTypes.SUB;
    }
}
