package dev.creoii.luckyblock.util.booleanprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.util.math.random.Random;

public class SimpleBooleanProvider extends BooleanProvider {
    public static final MapCodec<SimpleBooleanProvider> CODEC = Codec.BOOL.fieldOf("value").xmap(SimpleBooleanProvider::new, provider -> provider.value);
    private final boolean value;

    protected SimpleBooleanProvider(boolean value) {
        this.value = value;
    }

    protected BooleanProviderType<?> getType() {
        return BooleanProviderType.SIMPLE_BOOLEAN_PROVIDER;
    }

    @Override
    public boolean getBoolean(Outcome.Context<?> context, Random random) {
        return value;
    }
}
