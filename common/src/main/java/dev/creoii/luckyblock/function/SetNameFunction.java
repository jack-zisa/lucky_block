package dev.creoii.luckyblock.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.function.target.FunctionTarget;
import dev.creoii.luckyblock.function.target.HasNameFunctionTarget;
import dev.creoii.luckyblock.function.target.NameTarget;
import dev.creoii.luckyblock.function.target.Target;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.textprovider.TextProvider;

public class SetNameFunction extends Function<Target<?>> {
    @SuppressWarnings("unchecked")
    public static final MapCodec<SetNameFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(FunctionTarget.CODEC.fieldOf("target").orElse(HasNameFunctionTarget.INSTANCE).forGetter(Function::getTarget),
                TextProvider.CODEC.fieldOf("name").forGetter(function -> function.name)
        ).apply(instance, (functionTarget, name) -> new SetNameFunction((FunctionTarget<Target<?>>) functionTarget, name));
    });
    private final TextProvider name;

    protected SetNameFunction(FunctionTarget<Target<?>> target, TextProvider name) {
        super(FunctionType.SET_NAME, Phase.POST, target);
        this.name = name;
    }

    @Override
    public void apply(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        for (Target<?> target : target.getTargets(outcome, context)) {
            if (target instanceof NameTarget<?> nameTarget) {
                target.update(this, nameTarget.setName(outcome, context, name.get(context, context.random())));
            }
        }
    }
}
