package dev.creoii.luckyblock.util.textprovider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;

import java.util.List;

public class RandomTextProvider extends TextProvider {
    public static final MapCodec<RandomTextProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(TextProvider.CODEC.listOf().fieldOf("texts").forGetter(provider -> provider.texts)
        ).apply(instance, RandomTextProvider::new);
    });
    private final List<TextProvider> texts;

    public RandomTextProvider(List<TextProvider> texts) {
        this.texts = texts;
    }

    protected TextProviderType<?> getType() {
        return TextProviderType.RANDOM_TEXT_PROVIDER;
    }

    public Text get(Outcome.Context<?> context, Random random) {
        return texts.get(random.nextInt(texts.size())).get(context, random);
    }
}
