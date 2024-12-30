package dev.creoii.luckyblock.util.colorprovider;

import com.mojang.serialization.Codec;
import dev.creoii.luckyblock.LuckyBlockRegistries;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.random.Random;

public abstract class ColorProvider {
    public static final Codec<ColorProvider> TYPE_CODEC = LuckyBlockRegistries.COLOR_PROVIDER_TYPE.getCodec().dispatch(ColorProvider::getType, ColorProviderType::codec);

    public static SimpleColorProvider of(int color) {
        return new SimpleColorProvider(color);
    }

    public static SimpleColorProvider of(int[] rgb) {
        return new SimpleColorProvider(rgb);
    }

    public static SimpleColorProvider of(DyeColor dyeColor) {
        return new SimpleColorProvider(dyeColor);
    }

    protected abstract ColorProviderType<?> getType();

    public abstract int getInt(Random random);

    public abstract int[] getRgb(Random random);

    public abstract DyeColor getDyeColor(Random random);
}
