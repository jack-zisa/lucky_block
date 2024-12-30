package dev.creoii.luckyblock.function.target;

import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.util.math.floatprovider.FloatProvider;

public interface RotationTarget<T> extends Target<T> {
    T setRotation(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, FloatProvider pitch, FloatProvider yaw);
}
