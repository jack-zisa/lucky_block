package dev.creoii.luckyblock.util.function.target;

import net.minecraft.nbt.NbtElement;

public interface NbtTarget<T> extends Target<T> {
    T setNbt(NbtElement nbt);
}
