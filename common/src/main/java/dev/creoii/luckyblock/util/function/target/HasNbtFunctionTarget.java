package dev.creoii.luckyblock.util.function.target;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.function.wrapper.EntityWrapper;

import java.util.ArrayList;
import java.util.List;

public class HasNbtFunctionTarget extends FunctionTarget<Target<?>> {
    public static final HasNbtFunctionTarget INSTANCE = new HasNbtFunctionTarget();
    /**
     * Add options for block entities, entities, nbt item component types
     */
    public static final MapCodec<HasNbtFunctionTarget> CODEC = MapCodec.unit(INSTANCE);

    public HasNbtFunctionTarget() {
        super(FunctionTargetType.HAS_NBT);
    }

    @Override
    public List<Target<?>> getTargets(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        List<Target<?>> targets = new ArrayList<>();
        for (Object o : context.info().getTargets()) {
            if (o instanceof EntityWrapper entityWrapper) {
                targets.add(entityWrapper);
            }
            // block entities
        }
        return targets;
    }
}
