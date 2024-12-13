package dev.creoii.luckyblock.util.function.wrapper;

import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.function.target.Target;

public interface Wrapper<T, W> extends Target<W> {
    W init(Outcome.Context<?> context);

    T getRegistryObject(Outcome.Context<?> context);
}
