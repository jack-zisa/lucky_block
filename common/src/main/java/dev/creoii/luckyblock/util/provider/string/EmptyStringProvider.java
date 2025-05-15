package dev.creoii.luckyblock.util.provider.string;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.math.random.Random;

public class EmptyStringProvider extends StringProvider {
    public static final EmptyStringProvider EMPTY = new EmptyStringProvider();
    public static final MapCodec<EmptyStringProvider> CODEC = MapCodec.unit(EMPTY);

    @Override
    public String get(Random random) {
        return "";
    }

    @Override
    public StringProviderType<?> getType() {
        return StringProviderType.EMPTY;
    }
}
