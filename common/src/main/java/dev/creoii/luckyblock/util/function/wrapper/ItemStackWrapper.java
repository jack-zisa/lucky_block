package dev.creoii.luckyblock.util.function.wrapper;

import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.function.Function;
import dev.creoii.luckyblock.util.function.FunctionType;
import dev.creoii.luckyblock.util.function.Functions;
import dev.creoii.luckyblock.util.function.SetVelocityFunction;
import dev.creoii.luckyblock.util.function.target.ColorTarget;
import dev.creoii.luckyblock.util.function.target.ComponentsTarget;
import dev.creoii.luckyblock.util.function.target.CountTarget;
import dev.creoii.luckyblock.util.function.target.Target;
import dev.creoii.luckyblock.util.stackprovider.ItemStackProvider;
import dev.creoii.luckyblock.util.stackprovider.SimpleItemStackProvider;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.intprovider.IntProvider;

public record ItemStackWrapper(ItemStackProvider stackProvider, Functions functions) implements Wrapper<Item, ItemStackWrapper>, ComponentsTarget<ItemStackWrapper>, CountTarget<ItemStackWrapper>, ColorTarget<ItemStackWrapper> {
    public ItemStackWrapper(ItemStackProvider stackProvider, Functions functions) {
        this.stackProvider = stackProvider;

        if (!functions.has(FunctionType.SET_VELOCITY)) {
            this.functions = new Functions.Builder().addAll(functions).add(SetVelocityFunction.DEFAULT_ITEM_VELOCITY).build();
        } else this.functions = functions;
    }

    public static ItemStackWrapper fromStack(ItemStack stack) {
        return new ItemStackWrapper(SimpleItemStackProvider.of(stack), new Functions.Builder().add(SetVelocityFunction.DEFAULT_ITEM_VELOCITY).build());
    }

    @Override
    public Item getRegistryObject(Outcome.Context<?> context) {
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

    @Override
    public ItemStackWrapper setColor(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, int color) {
        ItemStack stack = stackProvider.get(context.world().getRandom());
        stack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(color, true));
        return this;
    }

    @Override
    public ItemStackWrapper setRgb(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, int[] rgb) {
        ItemStack stack = stackProvider.get(context.world().getRandom());
        stack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(ColorHelper.fromAbgr(ColorHelper.toAbgr(ColorHelper.getArgb(rgb[0], rgb[1], rgb[2]))), true));
        return this;
    }

    @Override
    public ItemStackWrapper setDyeColor(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, DyeColor dyeColor) {
        ItemStack stack = stackProvider.get(context.world().getRandom());
        stack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(dyeColor.getMapColor().color, true));
        return this;
    }
}