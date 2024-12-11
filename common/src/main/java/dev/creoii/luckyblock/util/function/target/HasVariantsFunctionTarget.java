package dev.creoii.luckyblock.util.function.target;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class HasVariantsFunctionTarget extends FunctionTarget<Target<?>> {
    public static final HasVariantsFunctionTarget INSTANCE = new HasVariantsFunctionTarget();
    public static final MapCodec<HasVariantsFunctionTarget> CODEC = MapCodec.unit(INSTANCE);

    public HasVariantsFunctionTarget() {
        super(FunctionTargetType.HAS_VARIANTS);
    }

    @Override
    public List<Target<?>> getTargets(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        List<Target<?>> targets = Lists.newArrayList();
        for (Object o : context.info().getTargets()) {
            if (o instanceof VariantTarget<?, ?> variantTarget)
                targets.add(variantTarget);
        }
        return targets;
    }
}
