package dev.creoii.luckyblock.function.target;

import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.util.math.Direction;

public interface DirectionTarget<T> extends Target<T> {
    T setDirection(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, Direction direction);
}
