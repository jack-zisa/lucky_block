package dev.creoii.luckyblock.util.booleanprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.util.math.random.Random;

public class WorldBooleanProvider extends BooleanProvider {
    public static final MapCodec<WorldBooleanProvider> CODEC = Codec.STRING.fieldOf("value").xmap(WorldBooleanProvider::new, provider -> provider.value);
    private final String value;

    protected WorldBooleanProvider(String value) {
        this.value = value;
    }

    protected BooleanProviderType<?> getType() {
        return BooleanProviderType.WORLD_BOOLEAN_PROVIDER;
    }

    @Override
    public boolean getBoolean(Outcome.Context<?> context, Random random) {
        return switch (value) {
            case "is_day" -> context.world().isDay();
            case "is_night" -> context.world().isNight();
            case "is_raining" -> context.world().isRaining();
            case "is_thundering" -> context.world().isThundering();
            default -> throw new IllegalArgumentException("Invalid world boolean type: " + value);
        };
    }
}
