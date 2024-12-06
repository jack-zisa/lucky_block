package dev.creoii.luckyblock.util.function;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;

public class EmptyFunction extends Function {
    public static final MapCodec<EmptyFunction> CODEC = MapCodec.unit(new EmptyFunction());

    protected EmptyFunction() {
        super(FunctionType.EMPTY);
    }

    public <T extends ContextInfo> void apply(Outcome<T> outcome, Outcome.Context<T> context) {}
}
