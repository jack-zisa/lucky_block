package dev.creoii.luckyblock.util.function.target;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.function.wrapper.ItemStackWrapper;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class CountFunctionTarget extends FunctionTarget<Target<?>> {
    public static final CountFunctionTarget INSTANCE = new CountFunctionTarget();
    public static final MapCodec<CountFunctionTarget> CODEC = MapCodec.unit(INSTANCE);

    public CountFunctionTarget() {
        super(FunctionTargetType.COUNT);
    }

    @Override
    public List<Target<?>> getTargets(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        List<Target<?>> targets = Lists.newArrayList();
        for (Object o : context.info().getTargets()) {
            if (o instanceof ItemStackWrapper itemStackWrapper)
                targets.add(itemStackWrapper);
            // entities
        }
        return targets;
    }
}
