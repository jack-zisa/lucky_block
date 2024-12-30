package dev.creoii.luckyblock.util.textprovider;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class SimpleTextProvider extends TextProvider {
    public static final MapCodec<SimpleTextProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(Codec.STRING.optionalFieldOf("text", "").forGetter(provider -> provider.text),
                Codec.STRING.optionalFieldOf("translation_key", "").forGetter(provider -> provider.translationKey),
                Codec.either(Formatting.CODEC, Formatting.CODEC.listOf()).xmap(either -> {
                    return either.map(List::of, Function.identity());
                }, Either::right).optionalFieldOf("formatting", List.of()).forGetter(provider -> provider.formatting)
        ).apply(instance, SimpleTextProvider::new);
    });
    @Nullable private final String text;
    @Nullable private final String translationKey;
    private final List<Formatting> formatting;

    public SimpleTextProvider(@Nullable String text, @Nullable String translationKey, List<Formatting> formatting) {
        this.text = text;
        this.translationKey = translationKey;
        this.formatting = formatting;
    }

    protected TextProviderType<?> getType() {
        return TextProviderType.SIMPLE_TEXT_PROVIDER;
    }

    public Text get(Random random) {
        if (text != null && !text.isEmpty()) {

            if (text.contains("§")) {
                String[] strings = text.split("(?=§)");
                //System.out.println(Arrays.toString(strings));
                List<TextProvider> texts = new ArrayList<>();

                for (String s : strings) {
                    if (s.isEmpty())
                        continue;

                    if (s.startsWith("§")) {
                        Formatting formatting1;
                        if ((formatting1 = Formatting.byCode(s.charAt(1))) == null)
                            continue;

                        String s1 = s.substring(2);
                        texts.add(new SimpleTextProvider(s1, null, List.of(formatting1)));
                    } else texts.add(new SimpleTextProvider(s, null, List.of()));
                }

                return new CompoundTextProvider(texts).get(random);
            }

            return net.minecraft.text.Text.literal(text).formatted(formatting.toArray(new Formatting[0]));
        } else if (translationKey != null && !translationKey.isEmpty()) {
            return net.minecraft.text.Text.translatable(translationKey).formatted(formatting.toArray(new Formatting[0]));
        } else return null;
    }

    /**
     * public List<TextComponent> parse(String input) {
     *         List<TextComponent> components = new ArrayList<>();
     *         StringBuilder currentText = new StringBuilder();
     *
     *         for (int i = 0; i < input.length(); i++) {
     *             char c = input.charAt(i);
     *
     *             if (c == '§' && i + 1 < input.length()) {
     *                 // Save current text as a component
     *                 if (currentText.length() > 0) {
     *                     components.add(new TextComponent(currentText.toString(), currentColor, currentStyles));
     *                     currentText.setLength(0);
     *                 }
     *
     *                 // Process the formatting code
     *                 char code = input.charAt(i + 1);
     *                 applyFormattingCode(code);
     *                 i++; // Skip the formatting code character
     *             } else {
     *                 // Append regular text
     *                 currentText.append(c);
     *             }
     *         }
     *
     *         // Add remaining text
     *         if (currentText.length() > 0) {
     *             components.add(new TextComponent(currentText.toString(), currentColor, currentStyles));
     *         }
     *
     *         return components;
     *     }
     *
     *     private void applyFormattingCode(char code) {
     *         if (COLOR_CODES.containsKey(code)) {
     *             currentColor = COLOR_CODES.get(code);
     *             currentStyles.clear(); // Reset styles on color change
     *         } else if (STYLE_CODES.containsKey(code)) {
     *             currentStyles.add(STYLE_CODES.get(code));
     *         } else if (code == 'r') {
     *             resetFormatting();
     *         }
     *     }
     *
     *     private void resetFormatting() {
     *         currentColor = null;
     *         currentStyles.clear();
     *     }
     */
}
