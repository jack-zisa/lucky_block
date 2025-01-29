package dev.creoii.luckyblock.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.function.target.FunctionTarget;
import dev.creoii.luckyblock.function.target.IsEntityFunctionTarget;
import dev.creoii.luckyblock.function.target.Target;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.function.wrapper.EntityWrapper;
import dev.creoii.luckyblock.util.entityprovider.EntityProvider;

import java.util.List;

public class AddPassengerFunction extends Function<Target<?>> {
    @SuppressWarnings("unchecked")
    public static final MapCodec<AddPassengerFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(FunctionTarget.CODEC.fieldOf("target").orElse(IsEntityFunctionTarget.DEFAULT).forGetter(Function::getTarget),
                EntityProvider.CODEC.fieldOf("passenger").forGetter(function -> function.passenger)
        ).apply(instance, (functionTarget, passenger) -> new AddPassengerFunction((FunctionTarget<Target<?>>) functionTarget, passenger));
    });
    private final EntityProvider passenger;

    protected AddPassengerFunction(FunctionTarget<Target<?>> target, EntityProvider passenger) {
        super(FunctionType.ADD_PASSENGER, Phase.POST, target);
        this.passenger = passenger;
    }

    int i = 0;

    @Override
    public Outcome.Context<? extends ContextInfo> apply(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        List<Target<?>> targets = target.getTargets(outcome, context);
        System.out.println(i++ + " size: " + targets.size());
        for (Target<?> target : targets) {
            if (target instanceof EntityWrapper wrapper) {
                List<EntityWrapper> passengerWrappers = passenger.getEntities(context, context.random());

                if (passengerWrappers == null || passengerWrappers.isEmpty())
                    return context;

                EntityWrapper prevEntity = null;
                for (EntityWrapper passengerWrapper : passengerWrappers) {
                    if (passengerWrapper.getEntity() == null) {
                        passengerWrapper = passengerWrapper.init(context);
                    }

                    if (wrapper.getEntity() == null) {
                        wrapper = wrapper.init(context);
                    }

                    passengerWrapper.getEntity().refreshPositionAndAngles(context.pos(), passengerWrapper.getEntity().getYaw(), passengerWrapper.getEntity().getPitch());
                    Function.applyAll(passengerWrapper.getFunctions(), outcome, context);

                    context.world().spawnEntity(passengerWrapper.getEntity());

                    if (wrapper.getEntity().hasPassengers()) {
                        passengerWrapper.getEntity().startRiding(wrapper.getEntity().getPassengerList().getLast(), true);
                    } else passengerWrapper.getEntity().startRiding(wrapper.getEntity(), true);

                    prevEntity = passengerWrapper;
                }
            }
        }
        return context.copyFiltered(targets);
    }
}
