package dev.creoii.luckyblock.util.provider.string;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.math.random.Random;

import java.util.List;

public class RandomStringProvider extends StringProvider {
    public static final Codec<RandomStringProvider> CODEC = Codec.STRING.listOf().fieldOf("values").xmap(RandomStringProvider::new, provider -> provider.values).codec();
    private final List<String> values;

    public RandomStringProvider(List<String> values) {
        this.values = values;
    }

    @Override
    public String get(Random random) {
        return values.get(random.nextInt(values.size()));
    }

    @Override
    public StringProviderType<?> getType() {
        return StringProviderType.RANDOM;
    }
}
