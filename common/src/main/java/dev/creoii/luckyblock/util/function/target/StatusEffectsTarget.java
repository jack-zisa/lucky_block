package dev.creoii.luckyblock.util.function.target;

import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.entity.effect.StatusEffectInstance;

public interface StatusEffectsTarget<T> extends Target<T> {
    T addStatusEffect(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, StatusEffectInstance statusEffectInstance);
}
