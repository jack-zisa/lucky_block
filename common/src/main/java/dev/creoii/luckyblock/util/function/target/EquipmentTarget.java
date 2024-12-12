package dev.creoii.luckyblock.util.function.target;

import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public interface EquipmentTarget<T> extends Target<T> {
    T setStack(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, EquipmentSlot slot, ItemStack stack);
}
