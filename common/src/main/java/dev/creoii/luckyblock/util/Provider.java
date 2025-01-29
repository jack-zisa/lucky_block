package dev.creoii.luckyblock.util;

public interface Provider<T> {
    T getParent();

    void setParent(T parent);
}
