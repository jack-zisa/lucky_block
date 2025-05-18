package dev.creoii.luckyblock.util.provider.booleanprovider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.random.Random;

public class AndBooleanProvider extends BooleanProvider {
    public static final MapCodec<AndBooleanProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(BooleanProvider.CODEC.fieldOf("a").forGetter(outcome -> outcome.a),
                BooleanProvider.CODEC.fieldOf("b").forGetter(outcome -> outcome.b)
        ).apply(instance, AndBooleanProvider::new);
    });
    public final BooleanProvider a;
    public final BooleanProvider b;

    public AndBooleanProvider(BooleanProvider a, BooleanProvider b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public boolean get(Random random) {
        return a.get(random) && b.get(random);
    }

    @Override
    public BooleanProviderType<?> getType() {
        return BooleanProviderType.AND;
    }
}
