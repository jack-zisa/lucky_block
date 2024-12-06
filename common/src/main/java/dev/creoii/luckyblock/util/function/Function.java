package dev.creoii.luckyblock.util.function;

import com.mojang.serialization.Codec;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;

public abstract class Function {
    public static final Codec<Function> CODEC = LuckyBlockMod.FUNCTION_TYPES.getCodec().dispatch(Function::getType, FunctionType::codec);
    private final FunctionType type;

    protected Function(FunctionType type) {
        this.type = type;
    }

    public FunctionType getType() {
        return type;
    }

    public abstract <T extends ContextInfo> void apply(Outcome<T> outcome, Outcome.Context<T> context);
}
