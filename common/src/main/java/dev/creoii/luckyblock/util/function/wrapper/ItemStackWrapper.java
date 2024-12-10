package dev.creoii.luckyblock.util.function.wrapper;

import com.google.common.collect.Lists;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.function.Function;
import dev.creoii.luckyblock.util.function.SetVelocityFunction;
import dev.creoii.luckyblock.util.function.target.ComponentsTarget;
import dev.creoii.luckyblock.util.function.target.CountTarget;
import dev.creoii.luckyblock.util.function.target.Target;
import dev.creoii.luckyblock.util.stackprovider.ItemStackProvider;
import dev.creoii.luckyblock.util.stackprovider.SimpleItemStackProvider;
import net.minecraft.component.ComponentChanges;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.intprovider.IntProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record ItemStackWrapper(ItemStackProvider stackProvider, List<Function<?>> functions) implements Wrapper<Item, ItemStackWrapper>, ComponentsTarget<ItemStackWrapper>, CountTarget<ItemStackWrapper> {
    public ItemStackWrapper(ItemStackProvider stackProvider, List<Function<?>> functions) {
        this.stackProvider = stackProvider;

        if (functions.stream().noneMatch(function -> function instanceof SetVelocityFunction)) {
            List<Function<?>> newFunctions = new ArrayList<>(functions);
            newFunctions.add(SetVelocityFunction.DEFAULT_ITEM_VELOCITY);
            this.functions = Collections.unmodifiableList(newFunctions);
        } else {
            this.functions = functions;
        }
    }

    public static ItemStackWrapper fromStack(ItemStack stack) {
        return new ItemStackWrapper(SimpleItemStackProvider.of(stack), Lists.newArrayList(SetVelocityFunction.DEFAULT_ITEM_VELOCITY));
    }

    @Override
    public Item getObject(Outcome.Context<?> context) {
        return stackProvider.get(context.world().getRandom()).getItem();
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
        stackProvider.get(context.world().getRandom()).applyChanges(componentChanges);
        return new ItemStackWrapper(stackProvider, functions);
    }

    @Override
    public ItemStackWrapper setCount(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, IntProvider count) {
        stackProvider.get(context.world().getRandom()).setCount(count.get(context.world().getRandom()));
        return new ItemStackWrapper(stackProvider, functions);
    }
}