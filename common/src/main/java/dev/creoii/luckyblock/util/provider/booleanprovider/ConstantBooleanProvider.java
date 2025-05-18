package dev.creoii.luckyblock.util.provider.booleanprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.math.random.Random;

public class ConstantBooleanProvider extends BooleanProvider {
    public static final MapCodec<ConstantBooleanProvider> CODEC = Codec.BOOL.fieldOf("value").xmap(ConstantBooleanProvider::new, provider -> provider.value);
    private final boolean value;

    public ConstantBooleanProvider(boolean value) {
        this.value = value;
    }

    @Override
    public boolean get(Random random) {
        return value;
    }

    @Override
    public BooleanProviderType<?> getType() {
        return BooleanProviderType.CONSTANT;
    }
}
