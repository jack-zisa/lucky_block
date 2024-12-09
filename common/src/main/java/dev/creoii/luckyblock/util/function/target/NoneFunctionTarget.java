package dev.creoii.luckyblock.util.function.target;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;

import java.util.List;

public class NoneFunctionTarget extends FunctionTarget<FunctionTarget.NoneTarget> {
    public static final NoneFunctionTarget INSTANCE = new NoneFunctionTarget();
    public static final MapCodec<NoneFunctionTarget> CODEC = MapCodec.unit(INSTANCE);

    public NoneFunctionTarget() {
        super(FunctionTargetType.NONE);
    }

    @Override
    public List<NoneTarget> getTargets(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        return List.of();
    }
}
