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
    private final Phase phase;
    protected FunctionTarget<T> target;

    protected Function(FunctionType type, Phase phase, FunctionTarget<T> target) {
        this.type = type;
        this.phase = phase;
        this.target = target;
    }

    public FunctionType getType() {
        return type;
    }

    public FunctionTarget<T> getTarget() {
        return target;
    }

    public abstract void apply(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context);

    public static void applyAll(FunctionContainer functionContainer, Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        functionContainer.forEach(function -> function.apply(outcome, context));
    }

    public static void applyPre(FunctionContainer functionContainer, Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        functionContainer.forEach(function -> function.apply(outcome, context), function -> function.phase == Phase.PRE);
    }

    public static void applyPost(FunctionContainer functionContainer, Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        functionContainer.forEach(function -> function.apply(outcome, context), function -> function.phase == Phase.POST);
    }

    public enum Phase {
        PRE,
        POST
    }
}
