package dev.creoii.luckyblock.util.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import dev.creoii.luckyblock.util.function.target.*;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.FloatProvider;

public class SetRotationFunction extends Function<Target<?>> {
    public static final SetRotationFunction DEFAULT_ENTITY_ROTATION = new SetRotationFunction(HasRotationFunctionTarget.INSTANCE, ConstantFloatProvider.ZERO, ConstantFloatProvider.ZERO);
    @SuppressWarnings("unchecked")
    public static final MapCodec<SetRotationFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(FunctionTarget.CODEC.fieldOf("target").orElse(HasRotationFunctionTarget.INSTANCE).forGetter(Function::getTarget),
                FloatProvider.VALUE_CODEC.fieldOf("pitch").orElse(ConstantFloatProvider.ZERO).forGetter(function -> function.pitch),
                FloatProvider.VALUE_CODEC.fieldOf("yaw").orElse(ConstantFloatProvider.ZERO).forGetter(function -> function.yaw)
        ).apply(instance, (functionTarget, pitch, yaw) -> new SetRotationFunction((FunctionTarget<Target<?>>) functionTarget, pitch, yaw));
    });
    private final FloatProvider pitch;
    private final FloatProvider yaw;

    protected SetRotationFunction(FunctionTarget<Target<?>> target, FloatProvider pitch, FloatProvider yaw) {
        super(FunctionType.SET_ROTATION, Phase.POST, target);
        this.pitch = pitch;
        this.yaw = yaw;
    }

    @Override
    public void apply(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        for (Target<?> target : target.getTargets(outcome, context)) {
            if (target instanceof RotationTarget<?> rotationTarget) {
                target.update(this, rotationTarget.setRotation(outcome, context, pitch, yaw));
            }
        }
    }
}
