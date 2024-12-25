package dev.creoii.luckyblock.util.function.target;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;

import java.util.List;

public class IsItemStackFunctionTarget extends FunctionTarget<Target<?>> {
    public static final IsItemStackFunctionTarget INSTANCE = new IsItemStackFunctionTarget();
    public static final MapCodec<IsItemStackFunctionTarget> CODEC = MapCodec.unit(INSTANCE);

    public IsItemStackFunctionTarget() {
        super(FunctionTargetType.IS_ITEM_STACK);
    }

    @Override
    public List<Target<?>> getTargets(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        return getItemStackTargets(context.info());
    }
}
