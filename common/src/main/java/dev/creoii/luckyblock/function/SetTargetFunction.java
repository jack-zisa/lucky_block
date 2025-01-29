package dev.creoii.luckyblock.function;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.function.target.FunctionTarget;
import dev.creoii.luckyblock.function.target.IsEntityFunctionTarget;
import dev.creoii.luckyblock.function.target.Target;
import dev.creoii.luckyblock.function.wrapper.EntityWrapper;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.MobEntity;

import java.util.List;

public class SetTargetFunction extends Function<Target<?>> {
    private static final SetTargetFunction DEFAULT = new SetTargetFunction(IsEntityFunctionTarget.DEFAULT);
    private static final MapCodec<SetTargetFunction> DEFAULT_CODEC = MapCodec.unit(DEFAULT);
    @SuppressWarnings("unchecked")
    private static final MapCodec<SetTargetFunction> BASE_CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(FunctionTarget.CODEC.fieldOf("target").orElse(IsEntityFunctionTarget.DEFAULT).forGetter(Function::getTarget)
        ).apply(instance, functionTarget -> new SetTargetFunction((FunctionTarget<Target<?>>) functionTarget));
    });
    public static final MapCodec<SetTargetFunction> CODEC = Codec.mapEither(DEFAULT_CODEC, BASE_CODEC).xmap(either -> {
        return either.map(java.util.function.Function.identity(), java.util.function.Function.identity());
    }, Either::right);

    protected SetTargetFunction(FunctionTarget<Target<?>> target) {
        super(FunctionType.SET_TARGET, Phase.POST, target);
    }

    @Override
    public Outcome.Context<? extends ContextInfo> apply(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        if (context.source() == null)
            return context;

        List<Target<?>> targets = target.getTargets(outcome, context);
        for (Target<?> target : targets) {
            if (target instanceof EntityWrapper wrapper && wrapper.getEntity() instanceof MobEntity mob) {
                mob.setTarget(context.source());

                if (wrapper.getEntity() instanceof Angerable angerable) {
                    angerable.chooseRandomAngerTime();
                    angerable.setTarget(context.source());
                }
            }
        }
        return context.copyFiltered(targets);
    }
}
