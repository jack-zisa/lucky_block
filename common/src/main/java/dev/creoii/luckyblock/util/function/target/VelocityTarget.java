package dev.creoii.luckyblock.util.function.target;

import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.util.math.Vec3d;

public interface VelocityTarget<T> extends Target<T> {
    T setVelocit(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, Vec3d velocity);
}
