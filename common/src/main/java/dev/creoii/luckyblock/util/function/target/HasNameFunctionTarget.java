package dev.creoii.luckyblock.util.function.target;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class HasNameFunctionTarget extends FunctionTarget<Target<?>> {
    public static final HasNameFunctionTarget INSTANCE = new HasNameFunctionTarget();
    public static final MapCodec<HasNameFunctionTarget> CODEC = MapCodec.unit(INSTANCE);

    public HasNameFunctionTarget() {
        super(FunctionTargetType.HAS_NAME);
    }

    @Override
    public List<Target<?>> getTargets(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        List<Target<?>> targets = Lists.newArrayList();
        for (Object o : context.info().getTargets()) {
            if (o instanceof NameTarget<?> colorTarget)
                targets.add(colorTarget);
        }
        return targets;
    }
}
