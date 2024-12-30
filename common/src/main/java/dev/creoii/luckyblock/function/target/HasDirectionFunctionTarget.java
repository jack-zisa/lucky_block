package dev.creoii.luckyblock.function.target;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class HasDirectionFunctionTarget extends FunctionTarget<Target<?>> {
    public static final HasDirectionFunctionTarget INSTANCE = new HasDirectionFunctionTarget();
    public static final MapCodec<HasDirectionFunctionTarget> CODEC = MapCodec.unit(INSTANCE);

    public HasDirectionFunctionTarget() {
        super(FunctionTargetType.HAS_DIRECTION);
    }

    @Override
    public List<Target<?>> getTargets(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        List<Target<?>> targets = Lists.newArrayList();
        for (Object o : context.info().getTargets()) {
            if (o instanceof DirectionTarget<?> directionTarget)
                targets.add(directionTarget);
        }
        return targets;
    }
}
