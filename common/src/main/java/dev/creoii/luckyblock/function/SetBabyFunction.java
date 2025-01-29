package dev.creoii.luckyblock.function;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.function.target.FunctionTarget;
import dev.creoii.luckyblock.function.target.IsEntityFunctionTarget;
import dev.creoii.luckyblock.function.target.Target;
import dev.creoii.luckyblock.function.wrapper.EntityWrapper;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.booleanprovider.BooleanProvider;
import net.minecraft.entity.passive.PassiveEntity;

import java.util.List;

public class SetBabyFunction extends Function<Target<?>> {
    private static final SetBabyFunction DEFAULT = new SetBabyFunction(IsEntityFunctionTarget.DEFAULT, BooleanProvider.TRUE);
    private static final MapCodec<SetBabyFunction> DEFAULT_CODEC = MapCodec.unit(DEFAULT);
    @SuppressWarnings("unchecked")
    public static final MapCodec<SetBabyFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(FunctionTarget.CODEC.fieldOf("target").orElse(IsEntityFunctionTarget.DEFAULT).forGetter(Function::getTarget),
                BooleanProvider.VALUE_CODEC.optionalFieldOf("baby", BooleanProvider.TRUE).forGetter(function -> function.baby)
        ).apply(instance, (functionTarget, baby) -> new SetBabyFunction((FunctionTarget<Target<?>>) functionTarget, baby));
    });
    private final BooleanProvider baby;

    protected SetBabyFunction(FunctionTarget<Target<?>> target, BooleanProvider baby) {
        super(FunctionType.SET_BABY, Phase.POST, target);
        this.baby = baby;
    }

    @Override
    public Outcome.Context<? extends ContextInfo> apply(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        List<Target<?>> targets = target.getTargets(outcome, context);
        for (Target<?> target : targets) {
            if (target instanceof EntityWrapper entityWrapper && entityWrapper.getEntity() instanceof PassiveEntity passive) {
                passive.setBaby(baby.getBoolean(context, context.random()));
            }
        }
        return context.copyFiltered(targets);
    }
}
