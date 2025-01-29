package dev.creoii.luckyblock.function.target;

import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.util.math.Vec3d;

public interface VecTarget<T> extends Target<T> {
    T setVec(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, Vec3d vec3d);
}
