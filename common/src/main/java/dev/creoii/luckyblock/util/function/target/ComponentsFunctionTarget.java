package dev.creoii.luckyblock.util.function.target;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;

import java.util.List;

public class ComponentsFunctionTarget extends FunctionTarget<Target<?>> {
    public static final ComponentsFunctionTarget INSTANCE = new ComponentsFunctionTarget();
    public static final MapCodec<ComponentsFunctionTarget> CODEC = MapCodec.unit(INSTANCE);

    public ComponentsFunctionTarget() {
        super(FunctionTargetType.COMPONENTS);
    }

    @Override
    public List<Target<?>> getTargets(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        return getItemStackTargets(context.info());
    }
}
