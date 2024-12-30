package dev.creoii.luckyblock.function.target;

import dev.creoii.luckyblock.function.Function;

public interface Target<T> {
    Target<T> update(Function<Target<?>> function, Object newObject);
}
