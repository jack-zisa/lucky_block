package dev.creoii.luckyblock.function;

import com.mojang.serialization.Codec;
import dev.creoii.luckyblock.LuckyBlockRegistries;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.function.target.FunctionTarget;
import dev.creoii.luckyblock.function.target.Target;

import java.util.Map;
import java.util.Optional;

public abstract class Function<T extends Target<?>> {
    public static final Codec<Function<?>> CODEC = LuckyBlockRegistries.FUNCTION_TYPES.getCodec().dispatch(Function::getType, FunctionType::codec);
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

    public abstract Outcome.Context<? extends ContextInfo> apply(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context);

    public static void applyAll(FunctionContainer functionContainer, Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        Outcome.Context<?> context1 = context;
        for (Map.Entry<FunctionType, Optional<Function<?>>> entry : functionContainer.functions().entrySet()) {
            Function<?> function = entry.getValue().orElseThrow();
            context1 = function.apply(outcome, context1);
        }
    }

    public static void applyPre(FunctionContainer functionContainer, Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        Outcome.Context<?> context1 = context;
        for (Map.Entry<FunctionType, Optional<Function<?>>> entry : functionContainer.functions().entrySet()) {
            Function<?> function = entry.getValue().orElseThrow();
            if (function.phase == Phase.PRE)
                context1 = function.apply(outcome, context1);
        }
    }

    public static void applyPost(FunctionContainer functionContainer, Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        Outcome.Context<?> context1 = context;
        for (Map.Entry<FunctionType, Optional<Function<?>>> entry : functionContainer.functions().entrySet()) {
            Function<?> function = entry.getValue().orElseThrow();
            if (function.phase == Phase.POST)
                context1 = function.apply(outcome, context1);
        }
    }

    public enum Phase {
        PRE,
        POST
    }
}
