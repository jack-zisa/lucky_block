package dev.creoii.luckyblock.util.textprovider;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Function;

public class WorldTextProvider extends TextProvider {
    public static final MapCodec<WorldTextProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(Codec.STRING.fieldOf("value").forGetter(provider -> provider.value),
                Codec.either(Formatting.CODEC, Formatting.CODEC.listOf()).xmap(either -> {
                    return either.map(List::of, Function.identity());
                }, Either::right).optionalFieldOf("formatting", List.of()).forGetter(provider -> provider.formatting)
        ).apply(instance, WorldTextProvider::new);
    });
    private final String value;
    private final List<Formatting> formatting;

    public WorldTextProvider(String value, List<Formatting> formatting) {
        this.value = value;
        this.formatting = formatting;
    }

    protected TextProviderType<?> getType() {
        return TextProviderType.WORLD_TEXT_PROVIDER;
    }

    public Text get(Outcome.Context<?> context, Random random) {
        return switch (value) {
            case "dimension" -> Text.of(StringUtils.capitalize(context.world().getDimensionEntry().getIdAsString().split(":")[1].replace('_', ' ')));
            case "world_name" -> Text.of(context.world().getServer().getSaveProperties().getLevelName());
            default -> throw new IllegalArgumentException("Invalid world text type: " + value);
        };
    }
}
