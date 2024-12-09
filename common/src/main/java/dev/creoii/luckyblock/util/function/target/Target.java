package dev.creoii.luckyblock.util.function.target;

import dev.creoii.luckyblock.util.function.Function;

public interface Target<T> {
    Target<T> update(Function<Target<?>> function, Object newObject);
}
