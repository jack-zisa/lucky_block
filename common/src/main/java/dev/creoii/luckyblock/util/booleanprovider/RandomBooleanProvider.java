package dev.creoii.luckyblock.util.booleanprovider;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.util.math.random.Random;

import java.util.List;

public class RandomBooleanProvider extends BooleanProvider {
    public static final List<BooleanProvider> ALL_VALUES = List.of(TRUE, FALSE);
    public static final RandomBooleanProvider DEFAULT = new RandomBooleanProvider(ALL_VALUES);
    public static final MapCodec<RandomBooleanProvider> CODEC = BooleanProvider.VALUE_CODEC.listOf().optionalFieldOf("values", ALL_VALUES).xmap(RandomBooleanProvider::new, RandomBooleanProvider::getProviders);
    private final List<BooleanProvider> providers;

    public List<BooleanProvider> getProviders() {
        return providers;
    }

    public RandomBooleanProvider(List<BooleanProvider> providers) {
        this.providers = providers;
    }

    protected BooleanProviderType<?> getType() {
        return BooleanProviderType.RANDOM_BOOLEAN_PROVIDER;
    }

    @Override
    public boolean getBoolean(Outcome.Context<?> context, Random random) {
        return providers.get(random.nextInt(providers.size())).getBoolean(context, random);
    }
}
