package dev.creoii.luckyblock.util.colorprovider;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.random.Random;

import java.util.List;

public class RandomColorProvider extends ColorProvider {
    public static final List<ColorProvider> ALL_COLORS = List.of(SimpleColorProvider.of(DyeColor.BROWN), SimpleColorProvider.of(DyeColor.RED), SimpleColorProvider.of(DyeColor.ORANGE), SimpleColorProvider.of(DyeColor.YELLOW), SimpleColorProvider.of(DyeColor.LIME), SimpleColorProvider.of(DyeColor.GREEN), SimpleColorProvider.of(DyeColor.CYAN), SimpleColorProvider.of(DyeColor.BLUE), SimpleColorProvider.of(DyeColor.LIGHT_BLUE), SimpleColorProvider.of(DyeColor.PINK), SimpleColorProvider.of(DyeColor.MAGENTA), SimpleColorProvider.of(DyeColor.PURPLE), SimpleColorProvider.of(DyeColor.BLACK), SimpleColorProvider.of(DyeColor.GRAY), SimpleColorProvider.of(DyeColor.LIGHT_GRAY), SimpleColorProvider.of(DyeColor.WHITE));
    public static final RandomColorProvider DEFAULT = new RandomColorProvider(ALL_COLORS);
    public static final MapCodec<RandomColorProvider> CODEC = ColorProvider.TYPE_CODEC.listOf().fieldOf("colors").orElse(ALL_COLORS).xmap(RandomColorProvider::new, RandomColorProvider::getProviders);
    private final List<ColorProvider> providers;

    public RandomColorProvider(List<ColorProvider> providers) {
        this.providers = providers;
    }

    public List<ColorProvider> getProviders() {
        return providers;
    }

    protected ColorProviderType<?> getType() {
        return ColorProviderType.RANDOM_COLOR_PROVIDER;
    }

    @Override
    public int getInt(Random random) {
        return providers.get(random.nextInt(providers.size())).getInt(random);
    }

    @Override
    public int[] getRgb(Random random) {
        return providers.get(random.nextInt(providers.size())).getRgb(random);
    }

    @Override
    public DyeColor getDyeColor(Random random) {
        return providers.get(random.nextInt(providers.size())).getDyeColor(random);
    }
}
