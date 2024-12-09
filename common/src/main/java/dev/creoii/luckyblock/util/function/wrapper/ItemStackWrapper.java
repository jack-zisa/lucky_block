package dev.creoii.luckyblock.util.function.wrapper;

import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.function.Function;
import dev.creoii.luckyblock.util.function.target.ComponentsTarget;
import dev.creoii.luckyblock.util.function.target.CountTarget;
import dev.creoii.luckyblock.util.function.target.Target;
import net.minecraft.component.ComponentChanges;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.intprovider.IntProvider;

import java.util.List;

public record ItemStackWrapper(ItemStack stack, List<Function<?>> functions) implements ComponentsTarget<ItemStackWrapper>, CountTarget<ItemStackWrapper> {
    public static ItemStackWrapper fromStack(ItemStack stack) {
        return new ItemStackWrapper(stack, List.of());
    }

    public ItemStack toStack(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        functions.forEach(function -> function.apply(outcome, context));
        return stack;
    }

    @Override
    public Target<ItemStackWrapper> update(Function<Target<?>> function, Object newObject) {
        if (newObject instanceof ItemStackWrapper newStack) {
            //functions.remove(function);
            //newStack.functions.remove(function);
            return newStack;
        }
        throw new IllegalArgumentException("Attempted updating itemstack target with non-itemstack value.");
    }

    @Override
    public ItemStackWrapper setComponents(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, ComponentChanges componentChanges) {
        stack.applyChanges(componentChanges);
        return new ItemStackWrapper(stack, functions);
    }

    @Override
    public ItemStackWrapper setCount(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, IntProvider count) {
        stack.setCount(count.get(context.world().getRandom()));
        return new ItemStackWrapper(stack, functions);
    }
}