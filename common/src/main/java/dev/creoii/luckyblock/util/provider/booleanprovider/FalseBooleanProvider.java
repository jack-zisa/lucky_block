package dev.creoii.luckyblock.util.provider.booleanprovider;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.math.random.Random;

public class FalseBooleanProvider extends BooleanProvider {
    public static final FalseBooleanProvider FALSE = new FalseBooleanProvider();
    public static final MapCodec<FalseBooleanProvider> CODEC = MapCodec.unit(FALSE);

    @Override
    public boolean get(Random random) {
        return false;
    }

    @Override
    public BooleanProviderType<?> getType() {
        return BooleanProviderType.FALSE;
    }
}
