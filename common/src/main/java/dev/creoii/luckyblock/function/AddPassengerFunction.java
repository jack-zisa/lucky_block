package dev.creoii.luckyblock.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.function.target.FunctionTarget;
import dev.creoii.luckyblock.function.target.IsEntityFunctionTarget;
import dev.creoii.luckyblock.function.target.Target;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.function.wrapper.EntityWrapper;

public class AddPassengerFunction extends Function<Target<?>> {
    @SuppressWarnings("unchecked")
    public static final MapCodec<AddPassengerFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(FunctionTarget.CODEC.fieldOf("target").orElse(IsEntityFunctionTarget.DEFAULT).forGetter(Function::getTarget),
                FunctionObjectCodecs.ENTITY_WRAPPER.fieldOf("passenger").forGetter(function -> function.passenger)
        ).apply(instance, (functionTarget, passenger) -> new AddPassengerFunction((FunctionTarget<Target<?>>) functionTarget, passenger));
    });
    private EntityWrapper passenger;

    protected AddPassengerFunction(FunctionTarget<Target<?>> target, EntityWrapper passenger) {
        super(FunctionType.ADD_PASSENGER, Phase.POST, target);
        this.passenger = passenger;
    }

    @Override
    public void apply(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        for (Target<?> target : target.getTargets(outcome, context)) {
            if (target instanceof EntityWrapper wrapper) {
                if (passenger.getEntity() == null) {
                    passenger = passenger.init(context);
                }

                if (wrapper.getEntity() == null) {
                    wrapper = wrapper.init(context);
                }

                Function.applyAll(passenger.getFunctions(), outcome, context);
                passenger.getEntity().refreshPositionAndAngles(context.pos(), passenger.getEntity().getYaw(), passenger.getEntity().getPitch());

                if (wrapper.getEntity().hasPassengers()) {
                    passenger.getEntity().startRiding(wrapper.getEntity().getPassengerList().getLast(), true);
                } else passenger.getEntity().startRiding(wrapper.getEntity(), true);

                context.world().spawnEntity(passenger.getEntity());
            }
        }
    }
}
