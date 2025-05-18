package dev.creoii.luckyblock.util.provider.stringprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.math.random.Random;

public class ConstantStringProvider extends StringProvider {
    public static final MapCodec<ConstantStringProvider> CODEC = Codec.STRING.fieldOf("value").xmap(ConstantStringProvider::new, provider -> provider.value);
    private final String value;

    public ConstantStringProvider(String value) {
        this.value = value;
    }

    @Override
    public String get(Random random) {
        return value;
    }

    @Override
    public StringProviderType<?> getType() {
        return StringProviderType.CONSTANT;
    }
}
