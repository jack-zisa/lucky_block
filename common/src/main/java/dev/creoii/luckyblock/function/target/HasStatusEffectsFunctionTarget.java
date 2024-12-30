package dev.creoii.luckyblock.function.target;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;

import java.util.List;

public class HasStatusEffectsFunctionTarget extends FunctionTarget<Target<?>> {
    public static final HasStatusEffectsFunctionTarget INSTANCE = new HasStatusEffectsFunctionTarget();
    public static final MapCodec<HasStatusEffectsFunctionTarget> CODEC = MapCodec.unit(INSTANCE);

    public HasStatusEffectsFunctionTarget() {
        super(FunctionTargetType.IS_ENTITY);
    }

    @Override
    public List<Target<?>> getTargets(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        return getEntityTargets(context.info());
    }
}
