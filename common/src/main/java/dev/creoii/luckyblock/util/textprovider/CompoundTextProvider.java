package dev.creoii.luckyblock.util.textprovider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.util.math.random.Random;

import java.util.List;

public class CompoundTextProvider extends TextProvider {
    public static final MapCodec<CompoundTextProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(TextProvider.CODEC.listOf().fieldOf("texts").forGetter(provider -> provider.texts)
        ).apply(instance, CompoundTextProvider::new);
    });
    private final List<TextProvider> texts;

    public CompoundTextProvider(List<TextProvider> texts) {
        this.texts = texts;
    }

    protected TextProviderType<?> getType() {
        return TextProviderType.COMPOUND_TEXT_PROVIDER;
    }

    public Text get(Random random) {
        MutableText text = texts.getFirst().get(random).copy();
        for (int i = 1; i < texts.size(); ++i) {
            text.append(texts.get(i).get(random));
        }
        return text;
    }
}
