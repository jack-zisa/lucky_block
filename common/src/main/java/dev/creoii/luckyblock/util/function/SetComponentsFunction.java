package dev.creoii.luckyblock.util.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.function.target.*;
import net.minecraft.component.ComponentChanges;

public class SetComponentsFunction extends Function<Target<?>> {
    @SuppressWarnings("unchecked")
    public static final MapCodec<SetComponentsFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(FunctionTarget.CODEC.fieldOf("target").orElse(HasComponentsFunctionTarget.INSTANCE).forGetter(Function::getTarget),
                ComponentChanges.CODEC.fieldOf("components").forGetter(function -> function.components)
        ).apply(instance, (functionTarget, nbtElement) -> new SetComponentsFunction((FunctionTarget<Target<?>>) functionTarget, nbtElement));
    });
    private final ComponentChanges components;

    protected SetComponentsFunction(FunctionTarget<Target<?>> target, ComponentChanges components) {
        super(FunctionType.SET_COMPONENTS, Phase.POST, target);
        this.components = components;
    }

    @Override
    public void apply(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        for (Target<?> target : target.getTargets(outcome, context)) {
            if (target instanceof ComponentsTarget<?> componentsTarget) {
                if (components != ComponentChanges.EMPTY)
                    target.update(this, componentsTarget.setComponents(outcome, context, components));
            }
        }
    }
}
