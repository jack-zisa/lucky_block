package dev.creoii.luckyblock.util.textprovider;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.creoii.luckyblock.LuckyBlockMod;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;

import java.util.List;
import java.util.function.Function;

public abstract class TextProvider {
    public static final Codec<TextProvider> TYPE_CODEC = LuckyBlockMod.TEXT_PROVIDER_TYPE.getCodec().dispatch(TextProvider::getType, TextProviderType::codec);
    public static final Codec<TextProvider> CODEC = Codec.either(Codec.STRING, TYPE_CODEC).xmap(either -> {
        return either.map(TextProvider::of, Function.identity());
    }, Either::right);

    public static SimpleTextProvider of(String text) {
        return new SimpleTextProvider(text, null, List.of());
    }

    protected abstract TextProviderType<?> getType();

    public abstract Text get(Random random);
}
