package dev.creoii.luckyblock.function;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.function.target.FunctionTarget;
import dev.creoii.luckyblock.function.target.HasStatusEffectsFunctionTarget;
import dev.creoii.luckyblock.function.target.StatusEffectsTarget;
import dev.creoii.luckyblock.function.target.Target;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.List;

public class AddStatusEffectsFunction extends Function<Target<?>> {
    @SuppressWarnings("unchecked")
    public static final MapCodec<AddStatusEffectsFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(FunctionTarget.CODEC.fieldOf("target").orElse(HasStatusEffectsFunctionTarget.INSTANCE).forGetter(Function::getTarget),
                Codec.either(StatusEffectInstance.CODEC, StatusEffectInstance.CODEC.listOf()).xmap(either -> {
                    return either.map(List::of, java.util.function.Function.identity());
                }, Either::right).fieldOf("status_effects").forGetter(function -> function.statusEffects)
        ).apply(instance, (functionTarget, passenger) -> new AddStatusEffectsFunction((FunctionTarget<Target<?>>) functionTarget, passenger));
    });
    private final List<StatusEffectInstance> statusEffects;

    protected AddStatusEffectsFunction(FunctionTarget<Target<?>> target, List<StatusEffectInstance> statusEffects) {
        super(FunctionType.ADD_STATUS_EFFECTS, Phase.POST, target);
        this.statusEffects = statusEffects;
    }

    @Override
    public Outcome.Context<? extends ContextInfo> apply(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        List<Target<?>> targets = target.getTargets(outcome, context);
        for (Target<?> target : targets) {
            if (target instanceof StatusEffectsTarget<?> statusEffectsTarget) {
                for (StatusEffectInstance statusEffectInstance : statusEffects)
                    target.update(this, statusEffectsTarget.addStatusEffect(outcome, context, statusEffectInstance));
            }
        }
        return context.copyFiltered(targets);
    }
}
