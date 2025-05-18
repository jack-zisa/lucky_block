package dev.creoii.luckyblock.util.provider.stringprovider;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.math.random.Random;

import java.util.List;

public class RandomStringProvider extends StringProvider {
    public static final MapCodec<RandomStringProvider> CODEC = StringProvider.CODEC.listOf().fieldOf("values").xmap(RandomStringProvider::new, provider -> provider.values);
    public final List<StringProvider> values;

    public RandomStringProvider(List<StringProvider> values) {
        this.values = values;
    }

    @Override
    public String get(Random random) {
        return values.get(random.nextInt(values.size())).get(random);
    }

    @Override
    public StringProviderType<?> getType() {
        return StringProviderType.RANDOM;
    }
}
