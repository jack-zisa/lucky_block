package dev.creoii.luckyblock.util.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.colorprovider.ColorProvider;
import dev.creoii.luckyblock.util.function.target.*;

public class SetColorFunction extends Function<Target<?>> {
    @SuppressWarnings("unchecked")
    public static final MapCodec<SetColorFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(FunctionTarget.CODEC.fieldOf("target").orElse(HasColorFunctionTarget.INSTANCE).forGetter(Function::getTarget),
                ColorProvider.TYPE_CODEC.fieldOf("color").forGetter(function -> function.color)
        ).apply(instance, (functionTarget, count) -> new SetColorFunction((FunctionTarget<Target<?>>) functionTarget, count));
    });
    private final ColorProvider color;

    protected SetColorFunction(FunctionTarget<Target<?>> target, ColorProvider color) {
        super(FunctionType.SET_COLOR, Phase.POST, target);
        this.color = color;
    }

    @Override
    public void apply(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        for (Target<?> target : target.getTargets(outcome, context)) {
            if (target instanceof ColorTarget<?> colorTarget) {
                //target.update(this, colorTarget.setColor(outcome, context, color.getInt(context.world().getRandom())));
                //target.update(this, colorTarget.setRgb(outcome, context, color.getRgb(context.world().getRandom())));
                target.update(this, colorTarget.setDyeColor(outcome, context, color.getDyeColor(context.world().getRandom())));
            }
        }
    }
}
