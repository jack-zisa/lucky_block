package dev.creoii.luckyblock.util.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.function.target.*;
import net.minecraft.util.math.intprovider.IntProvider;

public class SetCountFunction extends Function<Target<?>> {
    @SuppressWarnings("unchecked")
    public static final MapCodec<SetCountFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(FunctionTarget.CODEC.fieldOf("target").orElse(CountFunctionTarget.INSTANCE).forGetter(Function::getTarget),
                IntProvider.POSITIVE_CODEC.fieldOf("count").forGetter(function -> function.count)
        ).apply(instance, (functionTarget, count) -> new SetCountFunction((FunctionTarget<Target<?>>) functionTarget, count));
    });
    private final IntProvider count;

    protected SetCountFunction(FunctionTarget<Target<?>> target, IntProvider count) {
        super(FunctionType.SET_COUNT, target);
        this.count = count;
    }

    @Override
    public void apply(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        for (Target<?> target : target.getTargets(outcome, context)) {
            if (target instanceof CountTarget<?> countTarget) {
                target.update(this, countTarget.setCount(outcome, context, count));
            }
        }
    }
}
