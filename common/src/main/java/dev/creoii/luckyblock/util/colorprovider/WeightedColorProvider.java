package dev.creoii.luckyblock.util.colorprovider;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.DyeColor;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.random.Random;

public class WeightedColorProvider extends ColorProvider {
    public static final MapCodec<WeightedColorProvider> CODEC = DataPool.createCodec(ColorProvider.TYPE_CODEC).comapFlatMap(WeightedColorProvider::wrap, provider -> {
        return provider.providers;
    }).fieldOf("entries");
    private final DataPool<ColorProvider> providers;

    private static DataResult<WeightedColorProvider> wrap(DataPool<ColorProvider> providers) {
        return providers.isEmpty() ? DataResult.error(() -> {
            return "WeightedColorProvider with no colors";
        }) : DataResult.success(new WeightedColorProvider(providers));
    }

    public WeightedColorProvider(DataPool<ColorProvider> providers) {
        this.providers = providers;
    }

    public WeightedColorProvider(DataPool.Builder<ColorProvider> providers) {
        this(providers.build());
    }

    protected ColorProviderType<?> getType() {
        return ColorProviderType.WEIGHTED_COLOR_PROVIDER;
    }

    @Override
    public int getInt(Random random) {
        return providers.getDataOrEmpty(random).orElseThrow(IllegalStateException::new).getInt(random);
    }

    @Override
    public int[] getRgb(Random random) {
        return providers.getDataOrEmpty(random).orElseThrow(IllegalStateException::new).getRgb(random);
    }

    @Override
    public DyeColor getDyeColor(Random random) {
        return providers.getDataOrEmpty(random).orElseThrow(IllegalStateException::new).getDyeColor(random);
    }
}
