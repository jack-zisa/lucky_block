package dev.creoii.luckyblock.util.provider.booleanprovider;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.math.random.Random;

public class RandomBooleanProvider extends BooleanProvider {
    public static final RandomBooleanProvider RANDOM = new RandomBooleanProvider();
    public static final MapCodec<RandomBooleanProvider> CODEC = MapCodec.unit(RANDOM);

    @Override
    public boolean get(Random random) {
        return random.nextBoolean();
    }

    @Override
    public BooleanProviderType<?> getType() {
        return BooleanProviderType.RANDOM;
    }
}
