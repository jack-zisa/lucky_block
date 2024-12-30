package dev.creoii.luckyblock.function.target;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;

import java.util.List;

public class HasComponentsFunctionTarget extends FunctionTarget<Target<?>> {
    public static final HasComponentsFunctionTarget INSTANCE = new HasComponentsFunctionTarget();
    public static final MapCodec<HasComponentsFunctionTarget> CODEC = MapCodec.unit(INSTANCE);

    /**
     * Add options for if an object has matching components, instead of just searching for anything that can have components
     */

    public HasComponentsFunctionTarget() {
        super(FunctionTargetType.HAS_COMPONENTS);
    }

    @Override
    public List<Target<?>> getTargets(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        return getItemStackTargets(context.info());
    }
}
