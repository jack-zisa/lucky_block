package dev.creoii.luckyblock.util.textprovider;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.function.wrapper.EntityWrapper;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.entityprovider.EntityProvider;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.random.Random;

import java.util.List;
import java.util.function.Function;

public class EntityTextProvider extends TextProvider {
    public static final MapCodec<EntityTextProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(EntityProvider.CODEC.fieldOf("entity").forGetter(provider -> provider.entity),
                Codec.STRING.fieldOf("value").forGetter(provider -> provider.value),
                Codec.either(Formatting.CODEC, Formatting.CODEC.listOf()).xmap(either -> {
                    return either.map(List::of, Function.identity());
                }, Either::right).optionalFieldOf("formatting", List.of()).forGetter(provider -> provider.formatting)
        ).apply(instance, EntityTextProvider::new);
    });
    private final EntityProvider entity;
    private final String value;
    private final List<Formatting> formatting;

    public EntityTextProvider(EntityProvider entity, String value, List<Formatting> formatting) {
        this.entity = entity;
        this.value = value;
        this.formatting = formatting;
    }

    protected TextProviderType<?> getType() {
        return TextProviderType.ENTITY_TEXT_PROVIDER;
    }

    public Text get(Outcome.Context<?> context, Random random) {
        EntityWrapper wrapper = entity.getEntity(context, random);
        if (wrapper == null) {
            return ScreenTexts.EMPTY;
        }

        if (wrapper.getEntity() == null) {
            wrapper = wrapper.init(context);
        }

        return switch (value) {
            case "custom_name" -> wrapper.getEntity().getCustomName();
            case "display_name" -> wrapper.getEntity().getDisplayName();
            case "name" -> wrapper.getEntity().getName();
            default -> throw new IllegalStateException("Unexpected value: " + value);
        };
    }
}
