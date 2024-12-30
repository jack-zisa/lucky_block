package dev.creoii.luckyblock.function.wrapper;

import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.function.target.Target;

public interface Wrapper<T, W> extends Target<W> {
    W init(Outcome.Context<?> context);

    T getRegistryObject(Outcome.Context<?> context);
}
