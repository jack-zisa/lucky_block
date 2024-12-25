package dev.creoii.luckyblock.util.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.function.target.*;
import dev.creoii.luckyblock.util.function.wrapper.EntityWrapper;

public class AddPassengerFunction extends Function<Target<?>> {
    @SuppressWarnings("unchecked")
    public static final MapCodec<AddPassengerFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(FunctionTarget.CODEC.fieldOf("target").orElse(IsEntityFunctionTarget.DEFAULT).forGetter(Function::getTarget),
                FunctionObjectCodecs.ENTITY_WRAPPER.fieldOf("passenger").forGetter(function -> function.passenger)
        ).apply(instance, (functionTarget, passenger) -> new AddPassengerFunction((FunctionTarget<Target<?>>) functionTarget, passenger));
    });
    private final EntityWrapper passenger;

    protected AddPassengerFunction(FunctionTarget<Target<?>> target, EntityWrapper passenger) {
        super(FunctionType.ADD_PASSENGER, Phase.POST, target);
        this.passenger = passenger;
    }

    @Override
    public void apply(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        for (Target<?> target : target.getTargets(outcome, context)) {
            if (target instanceof PassengersTarget<?> passengersTarget) {
                target.update(this, passengersTarget.addPassenger(outcome, context, passenger));
            }
        }
    }
}
