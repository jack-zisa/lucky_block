package dev.creoii.luckyblock.util.provider.booleanprovider;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.math.random.Random;

public class TrueBooleanProvider extends BooleanProvider {
    public static final TrueBooleanProvider TRUE = new TrueBooleanProvider();
    public static final MapCodec<TrueBooleanProvider> CODEC = MapCodec.unit(TRUE);

    @Override
    public boolean get(Random random) {
        return true;
    }

    @Override
    public BooleanProviderType<?> getType() {
        return BooleanProviderType.TRUE;
    }
}
