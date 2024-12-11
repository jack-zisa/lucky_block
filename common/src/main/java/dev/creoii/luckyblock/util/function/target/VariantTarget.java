package dev.creoii.luckyblock.util.function.target;

import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.util.DyeColor;
import net.minecraft.util.StringIdentifiable;

public interface VariantTarget<T, V> extends Target<T> {
    T setVariant(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, V variant);
}
