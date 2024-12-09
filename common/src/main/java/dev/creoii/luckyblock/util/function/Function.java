package dev.creoii.luckyblock.util.function;

import com.mojang.serialization.Codec;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.function.target.FunctionTarget;
import dev.creoii.luckyblock.util.function.target.Target;

public abstract class Function<T extends Target<?>> {
    public static final Codec<Function<?>> CODEC = LuckyBlockMod.FUNCTION_TYPES.getCodec().dispatch(Function::getType, FunctionType::codec);
    private final FunctionType type;
    protected final FunctionTarget<T> target;

    protected Function(FunctionType type, FunctionTarget<T> target) {
        this.type = type;
        this.target = target;
    }

    public FunctionType getType() {
        return type;
    }

    public FunctionTarget<T> getTarget() {
        return target;
    }

    public abstract void apply(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context);
}
