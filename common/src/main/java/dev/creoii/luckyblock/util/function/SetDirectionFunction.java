package dev.creoii.luckyblock.util.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.function.target.*;
import net.minecraft.util.math.Direction;

public class SetDirectionFunction extends Function<Target<?>> {
    public static final SetDirectionFunction DEFAULT_DIRECTION = new SetDirectionFunction(HasDirectionFunctionTarget.INSTANCE, Direction.NORTH);
    @SuppressWarnings("unchecked")
    public static final MapCodec<SetDirectionFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(FunctionTarget.CODEC.fieldOf("target").orElse(HasDirectionFunctionTarget.INSTANCE).forGetter(Function::getTarget),
                Direction.CODEC.fieldOf("direction").orElse(Direction.NORTH).forGetter(function -> function.direction)
        ).apply(instance, (functionTarget, direction) -> new SetDirectionFunction((FunctionTarget<Target<?>>) functionTarget, direction));
    });
    private final Direction direction;

    protected SetDirectionFunction(FunctionTarget<Target<?>> target, Direction direction) {
        super(FunctionType.SET_DIRECTION, Phase.POST, target);
        this.direction = direction;
    }

    @Override
    public void apply(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        for (Target<?> target : target.getTargets(outcome, context)) {
            if (target instanceof DirectionTarget<?> directionTarget) {
                target.update(this, directionTarget.setDirection(outcome, context, direction));
            }
        }
    }
}
