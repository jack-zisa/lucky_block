package dev.creoii.luckyblock.util.function.target;

import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.item.ItemStack;

public interface EquipmentTarget<T> extends Target<T> {
    T setHead(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, ItemStack head);

    T setChest(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, ItemStack chest);

    T setLegs(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, ItemStack legs);

    T setFeet(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, ItemStack boots);

    T setMainhand(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, ItemStack mainhand);

    T setOffhand(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, ItemStack offhand);

    T setBody(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, ItemStack body);
}
