package dev.creoii.luckyblock.function;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.function.target.FunctionTarget;
import dev.creoii.luckyblock.function.target.HasStatusEffectsFunctionTarget;
import dev.creoii.luckyblock.function.target.IsEntityFunctionTarget;
import dev.creoii.luckyblock.function.target.Target;
import dev.creoii.luckyblock.function.wrapper.EntityWrapper;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.entity.passive.PassiveEntity;

public class SetBabyFunction extends Function<Target<?>> {
    private static final SetBabyFunction DEFAULT = new SetBabyFunction(IsEntityFunctionTarget.DEFAULT);
    private static final MapCodec<SetBabyFunction> DEFAULT_CODEC = MapCodec.unit(DEFAULT);
    @SuppressWarnings("unchecked")
    private static final MapCodec<SetBabyFunction> BASE_CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(FunctionTarget.CODEC.fieldOf("target").orElse(IsEntityFunctionTarget.DEFAULT).forGetter(Function::getTarget)
        ).apply(instance, functionTarget -> new SetBabyFunction((FunctionTarget<Target<?>>) functionTarget));
    });
    public static final MapCodec<SetBabyFunction> CODEC = Codec.mapEither(DEFAULT_CODEC, BASE_CODEC).xmap(either -> {
        return either.map(java.util.function.Function.identity(), java.util.function.Function.identity());
    }, Either::right);

    protected SetBabyFunction(FunctionTarget<Target<?>> target) {
        super(FunctionType.SET_BABY, Phase.POST, target);
    }

    @Override
    public void apply(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        for (Target<?> target : target.getTargets(outcome, context)) {
            if (target instanceof EntityWrapper entityWrapper && entityWrapper.getEntity() instanceof PassiveEntity passive) {
                passive.setBaby(true);
            }
        }
    }
}
