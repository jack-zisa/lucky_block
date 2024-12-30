package dev.creoii.luckyblock.function.target;

import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.text.Text;

public interface NameTarget<T> extends Target<T> {
    T setName(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, Text name);
}
