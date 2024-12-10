package dev.creoii.luckyblock.util.function;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.function.target.FunctionTarget;
import dev.creoii.luckyblock.util.function.target.NoneFunctionTarget;

public class EmptyFunction extends Function<FunctionTarget.NoneTarget> {
    public static final MapCodec<EmptyFunction> CODEC = MapCodec.unit(new EmptyFunction());

    protected EmptyFunction() {
        super(FunctionType.EMPTY, Phase.PRE, NoneFunctionTarget.INSTANCE);
    }

    @Override
    public void apply(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {}
}
