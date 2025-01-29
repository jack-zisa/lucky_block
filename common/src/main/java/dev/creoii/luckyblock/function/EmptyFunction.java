package dev.creoii.luckyblock.function;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.function.target.Target;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.function.target.FunctionTarget;
import dev.creoii.luckyblock.function.target.NoneFunctionTarget;

import java.util.List;

public class EmptyFunction extends Function<FunctionTarget.NoneTarget> {
    public static final MapCodec<EmptyFunction> CODEC = MapCodec.unit(new EmptyFunction());

    protected EmptyFunction() {
        super(FunctionType.EMPTY, Phase.PRE, NoneFunctionTarget.INSTANCE);
    }

    @Override
    public Outcome.Context<? extends ContextInfo> apply(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        return context;
    }
}
