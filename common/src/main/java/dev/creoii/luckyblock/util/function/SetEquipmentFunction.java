package dev.creoii.luckyblock.util.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.function.target.*;
import dev.creoii.luckyblock.util.function.wrapper.ItemStackWrapper;
import dev.creoii.luckyblock.util.stackprovider.SimpleItemStackProvider;
import net.minecraft.entity.EquipmentSlot;

public class SetEquipmentFunction extends Function<Target<?>> {
    public static final ItemStackWrapper EMPTY = new ItemStackWrapper(new SimpleItemStackProvider(null), FunctionContainer.EMPTY);
    @SuppressWarnings("unchecked")
    public static final MapCodec<SetEquipmentFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(FunctionTarget.CODEC.fieldOf("target").orElse(HasEquipmentFunctionTarget.INSTANCE).forGetter(Function::getTarget),
                FunctionObjectCodecs.ITEM_STACK_WRAPPER.fieldOf("head_provider").orElse(EMPTY).forGetter(function -> function.headProvider),
                FunctionObjectCodecs.ITEM_STACK_WRAPPER.fieldOf("chest_provider").orElse(EMPTY).forGetter(function -> function.chestProvider),
                FunctionObjectCodecs.ITEM_STACK_WRAPPER.fieldOf("legs_provider").orElse(EMPTY).forGetter(function -> function.legsProvider),
                FunctionObjectCodecs.ITEM_STACK_WRAPPER.fieldOf("feet_provider").orElse(EMPTY).forGetter(function -> function.feetProvider),
                FunctionObjectCodecs.ITEM_STACK_WRAPPER.fieldOf("mainhand_provider").orElse(EMPTY).forGetter(function -> function.mainhandProvider),
                FunctionObjectCodecs.ITEM_STACK_WRAPPER.fieldOf("offhand_provider").orElse(EMPTY).forGetter(function -> function.offhandProvider),
                FunctionObjectCodecs.ITEM_STACK_WRAPPER.fieldOf("body_provider").orElse(EMPTY).forGetter(function -> function.bodyProvider)
        ).apply(instance, (functionTarget, headProvider, chestProvider, legsProvider, feetProvider, mainhandProvider, offhandProvider, bodyProvider) -> new SetEquipmentFunction((FunctionTarget<Target<?>>) functionTarget, headProvider, chestProvider, legsProvider, feetProvider, mainhandProvider, offhandProvider, bodyProvider));
    });
    private final ItemStackWrapper headProvider;
    private final ItemStackWrapper chestProvider;
    private final ItemStackWrapper legsProvider;
    private final ItemStackWrapper feetProvider;
    private final ItemStackWrapper mainhandProvider;
    private final ItemStackWrapper offhandProvider;
    private final ItemStackWrapper bodyProvider;

    protected SetEquipmentFunction(FunctionTarget<Target<?>> target, ItemStackWrapper headProvider, ItemStackWrapper chestProvider, ItemStackWrapper legsProvider, ItemStackWrapper feetProvider, ItemStackWrapper mainhandProvider, ItemStackWrapper offhandProvider, ItemStackWrapper bodyProvider) {
        super(FunctionType.SET_EQUIPMENT, Phase.POST, target);
        this.headProvider = headProvider;
        this.chestProvider = chestProvider;
        this.legsProvider = legsProvider;
        this.feetProvider = feetProvider;
        this.mainhandProvider = mainhandProvider;
        this.offhandProvider = offhandProvider;
        this.bodyProvider = bodyProvider;
    }

    @Override
    public void apply(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        for (Target<?> target : target.getTargets(outcome, context)) {
            if (target instanceof EquipmentTarget<?> equipmentTarget) {
                if (headProvider != EMPTY)
                    target.update(this, equipmentTarget.setStack(outcome, context, EquipmentSlot.HEAD, headProvider.getStackProvider().get(context.random())));
                if (chestProvider != EMPTY)
                    target.update(this, equipmentTarget.setStack(outcome, context, EquipmentSlot.CHEST, chestProvider.getStackProvider().get(context.random())));
                if (legsProvider != EMPTY)
                    target.update(this, equipmentTarget.setStack(outcome, context, EquipmentSlot.LEGS, legsProvider.getStackProvider().get(context.random())));
                if (feetProvider != EMPTY)
                    target.update(this, equipmentTarget.setStack(outcome, context, EquipmentSlot.FEET, feetProvider.getStackProvider().get(context.random())));
                if (mainhandProvider != EMPTY)
                    target.update(this, equipmentTarget.setStack(outcome, context, EquipmentSlot.MAINHAND, mainhandProvider.getStackProvider().get(context.random())));
                if (offhandProvider != EMPTY)
                    target.update(this, equipmentTarget.setStack(outcome, context, EquipmentSlot.OFFHAND, offhandProvider.getStackProvider().get(context.random())));
                if (bodyProvider != EMPTY)
                    target.update(this, equipmentTarget.setStack(outcome, context, EquipmentSlot.BODY, bodyProvider.getStackProvider().get(context.random())));
            }
        }
    }
}
