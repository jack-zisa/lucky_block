package dev.creoii.luckyblock.util.function.target;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class HasRotationFunctionTarget extends FunctionTarget<Target<?>> {
    public static final HasRotationFunctionTarget INSTANCE = new HasRotationFunctionTarget();
    public static final MapCodec<HasRotationFunctionTarget> CODEC = MapCodec.unit(INSTANCE);

    public HasRotationFunctionTarget() {
        super(FunctionTargetType.HAS_ROTATION);
    }

    @Override
    public List<Target<?>> getTargets(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        List<Target<?>> targets = Lists.newArrayList();
        for (Object o : context.info().getTargets()) {
            if (o instanceof RotationTarget<?> rotationTarget)
                targets.add(rotationTarget);
        }
        return targets;
    }
}
