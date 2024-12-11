package dev.creoii.luckyblock.util.function.wrapper;

import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.function.target.Target;

public interface Wrapper<T, W> extends Target<W> {
    T getRegistryObject(Outcome.Context<?> context);
}
