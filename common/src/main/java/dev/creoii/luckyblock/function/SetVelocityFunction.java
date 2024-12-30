package dev.creoii.luckyblock.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.function.target.FunctionTarget;
import dev.creoii.luckyblock.function.target.IsEntityFunctionTarget;
import dev.creoii.luckyblock.function.target.Target;
import dev.creoii.luckyblock.function.target.VelocityTarget;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.vecprovider.RandomVecProvider;
import dev.creoii.luckyblock.util.vecprovider.VecProvider;

public class SetVelocityFunction extends Function<Target<?>> {
    public static final SetVelocityFunction DEFAULT_ITEM_VELOCITY = new SetVelocityFunction(IsEntityFunctionTarget.DEFAULT, RandomVecProvider.DEFAULT_ITEM_VELOCITY);
    @SuppressWarnings("unchecked")
    public static final MapCodec<SetVelocityFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(FunctionTarget.CODEC.fieldOf("target").orElse(IsEntityFunctionTarget.DEFAULT).forGetter(Function::getTarget),
                VecProvider.VALUE_CODEC.fieldOf("velocity").orElse(RandomVecProvider.DEFAULT_ITEM_VELOCITY).forGetter(function -> function.velocity)
        ).apply(instance, (functionTarget, count) -> new SetVelocityFunction((FunctionTarget<Target<?>>) functionTarget, count));
    });
    private final VecProvider velocity;

    protected SetVelocityFunction(FunctionTarget<Target<?>> target, VecProvider velocity) {
        super(FunctionType.SET_VELOCITY, Phase.POST, target);
        this.velocity = velocity;
    }

    @Override
    public void apply(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        for (Target<?> target : target.getTargets(outcome, context)) {
            if (target instanceof VelocityTarget<?> countTarget) {
                target.update(this, countTarget.setVelocity(outcome, context, velocity));
            }
        }
    }
}
