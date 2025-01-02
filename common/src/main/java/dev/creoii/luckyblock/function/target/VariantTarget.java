package dev.creoii.luckyblock.function.target;

import com.mojang.datafixers.util.Either;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;

public interface VariantTarget<T> extends Target<T> {
    T setVariant(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, Either<Integer, String> variant);
}
