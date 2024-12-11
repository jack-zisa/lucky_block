package dev.creoii.luckyblock.util.colorprovider;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.stream.IntStream;

public class SimpleColorProvider extends ColorProvider {
    public static final MapCodec<SimpleColorProvider> DYE_COLOR_CODEC = DyeColor.CODEC.fieldOf("color").xmap(SimpleColorProvider::new, provider -> provider.dyeColor);
    public static final MapCodec<SimpleColorProvider> RGB_CODEC = Codec.INT_STREAM.fieldOf("color").xmap(intStream -> new SimpleColorProvider(intStream.toArray()), provider -> IntStream.of(ColorHelper.getRed(provider.color), ColorHelper.getGreen(provider.color), ColorHelper.getBlue(provider.color)));
    public static final MapCodec<SimpleColorProvider> COLOR_CODEC = Codec.INT.fieldOf("color").xmap(SimpleColorProvider::new, provider -> provider.color);

    public static final MapCodec<SimpleColorProvider> CODEC = Codec.mapEither(DYE_COLOR_CODEC, COLOR_CODEC).xmap(either -> {
        return either.map(Function.identity(), Function.identity());
    }, Either::right);
    private final int color;
    @Nullable
    private DyeColor dyeColor;

    protected SimpleColorProvider(int color) {
        this.color = color;
    }

    protected SimpleColorProvider(int[] rgb) {
        this.color = ColorHelper.fromAbgr(ColorHelper.toAbgr(ColorHelper.getArgb(rgb[0], rgb[1], rgb[2])));
    }

    protected SimpleColorProvider(DyeColor dyeColor) {
        this(dyeColor.getMapColor().color);
        this.dyeColor = dyeColor;
    }

    public int getColor() {
        return color;
    }

    protected ColorProviderType<?> getType() {
        return ColorProviderType.SIMPLE_COLOR_PROVIDER;
    }

    @Override
    public int getInt(Random random) {
        return color;
    }

    @Override
    public int[] getRgb(Random random) {
        return new int[]{ColorHelper.getRed(color), ColorHelper.getGreen(color), ColorHelper.getBlue(color)};
    }

    @Override
    public DyeColor getDyeColor(Random random) {
        return dyeColor;
    }
}
