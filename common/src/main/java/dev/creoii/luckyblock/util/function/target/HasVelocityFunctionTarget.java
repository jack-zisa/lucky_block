package dev.creoii.luckyblock.util.function.target;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;

import java.util.List;

public class HasVelocityFunctionTarget extends FunctionTarget<Target<?>> {
    public static final HasVelocityFunctionTarget INSTANCE = new HasVelocityFunctionTarget();
    public static final MapCodec<HasVelocityFunctionTarget> CODEC = MapCodec.unit(INSTANCE);

    public HasVelocityFunctionTarget() {
        super(FunctionTargetType.HAS_VELOCITY);
    }

    @Override
    public List<Target<?>> getTargets(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        return getEntityTargets(context.info());
    }
}
