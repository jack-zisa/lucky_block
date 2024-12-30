package dev.creoii.luckyblock.function.target;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class HasCountFunctionTarget extends FunctionTarget<Target<?>> {
    public static final HasCountFunctionTarget INSTANCE = new HasCountFunctionTarget();
    public static final MapCodec<HasCountFunctionTarget> CODEC = MapCodec.unit(INSTANCE);

    public HasCountFunctionTarget() {
        super(FunctionTargetType.HAS_COUNT);
    }

    @Override
    public List<Target<?>> getTargets(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        List<Target<?>> targets = Lists.newArrayList();
        for (Object o : context.info().getTargets()) {
            if (o instanceof CountTarget<?> countTarget)
                targets.add(countTarget);
        }
        return targets;
    }
}
