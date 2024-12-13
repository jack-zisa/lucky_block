package dev.creoii.luckyblock.util.function.target;

import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.function.wrapper.EntityWrapper;

public interface PassengersTarget<T> extends Target<T> {
    T addPassenger(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, EntityWrapper entity);
}
