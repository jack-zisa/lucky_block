package dev.creoii.luckyblock.util.function.target;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class HasColorFunctionTarget extends FunctionTarget<Target<?>> {
    public static final HasColorFunctionTarget INSTANCE = new HasColorFunctionTarget();
    public static final MapCodec<HasColorFunctionTarget> CODEC = MapCodec.unit(INSTANCE);

    public HasColorFunctionTarget() {
        super(FunctionTargetType.HAS_COLOR);
    }

    @Override
    public List<Target<?>> getTargets(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        List<Target<?>> targets = Lists.newArrayList();
        for (Object o : context.info().getTargets()) {
            if (o instanceof ColorTarget<?> colorTarget)
                targets.add(colorTarget);
        }
        return targets;
    }
}
