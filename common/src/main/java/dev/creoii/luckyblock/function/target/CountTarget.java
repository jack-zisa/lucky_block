package dev.creoii.luckyblock.function.target;

import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.util.math.intprovider.IntProvider;

public interface CountTarget<T> extends Target<T> {
    T setCount(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, IntProvider count);
}
