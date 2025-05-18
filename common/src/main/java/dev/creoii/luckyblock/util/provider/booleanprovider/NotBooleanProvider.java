package dev.creoii.luckyblock.util.provider.booleanprovider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.random.Random;

public class NotBooleanProvider extends BooleanProvider {
    public static final MapCodec<NotBooleanProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(BooleanProvider.CODEC.fieldOf("value").forGetter(outcome -> outcome.value)
        ).apply(instance, NotBooleanProvider::new);
    });
    public final BooleanProvider value;

    public NotBooleanProvider(BooleanProvider value) {
        this.value = value;
    }

    @Override
    public boolean get(Random random) {
        return !value.get(random);
    }

    @Override
    public BooleanProviderType<?> getType() {
        return BooleanProviderType.NOT;
    }
}
