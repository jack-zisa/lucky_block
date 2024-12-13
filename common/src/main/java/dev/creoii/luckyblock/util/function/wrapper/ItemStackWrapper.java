package dev.creoii.luckyblock.util.function.wrapper;

import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.function.Function;
import dev.creoii.luckyblock.util.function.FunctionType;
import dev.creoii.luckyblock.util.function.FunctionContainer;
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

public class ItemStackWrapper implements Wrapper<Item, ItemStackWrapper>, ComponentsTarget<ItemStackWrapper>, CountTarget<ItemStackWrapper>, ColorTarget<ItemStackWrapper> {
    public static final FunctionContainer DEFAULT_FUNCTIONS = new FunctionContainer.Builder().add(SetVelocityFunction.DEFAULT_ITEM_VELOCITY).build();
    private final FunctionContainer functionContainer;
    private final ItemStackProvider stackProvider;
    private final ItemStack stack;

    public ItemStackWrapper(ItemStackProvider stackProvider, FunctionContainer functionContainer, Outcome.Context<? extends ContextInfo> context) {
        this.stackProvider = stackProvider;
        this.stack = stackProvider.get(context.world().getRandom());

        if (!functionContainer.has(FunctionType.SET_VELOCITY)) {
            this.functionContainer = new FunctionContainer.Builder().addAll(functionContainer).add(SetVelocityFunction.DEFAULT_ITEM_VELOCITY).build();
        } else this.functionContainer = functionContainer;
    }

    public ItemStackWrapper(ItemStackProvider stackProvider, FunctionContainer functionContainer) {
        this.stackProvider = stackProvider;
        this.stack = null;

        if (functionContainer.has(FunctionType.SET_VELOCITY)) {
            this.functionContainer = functionContainer;
        } else this.functionContainer = new FunctionContainer.Builder().addAll(functionContainer).add(SetVelocityFunction.DEFAULT_ITEM_VELOCITY).build();;
    }

    public ItemStackWrapper(ItemStack stack) {
        this.functionContainer = DEFAULT_FUNCTIONS;
        this.stackProvider = SimpleItemStackProvider.of(stack);
        this.stack = stack;
    }

    public FunctionContainer getFunctionContainer() {
        return functionContainer;
    }

    public ItemStackProvider getStackProvider() {
        return stackProvider;
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public ItemStackWrapper init(Outcome.Context<?> context) {
        return new ItemStackWrapper(stackProvider, functionContainer, context);
    }

    @Override
    public Item getRegistryObject(Outcome.Context<?> context) {
        return stack.getItem();
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
        return this;
    }

    @Override
    public ItemStackWrapper setCount(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, IntProvider count) {
        stack.setCount(count.get(context.world().getRandom()));
        return this;
    }

    @Override
    public ItemStackWrapper setColor(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, int color) {
        stack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(color, true));
        return this;
    }

    @Override
    public ItemStackWrapper setRgb(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, int[] rgb) {
        stack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(ColorHelper.fromAbgr(ColorHelper.toAbgr(ColorHelper.getArgb(rgb[0], rgb[1], rgb[2]))), true));
        return this;
    }

    @Override
    public ItemStackWrapper setDyeColor(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context, DyeColor dyeColor) {
        stack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(dyeColor.getMapColor().color, true));
        return this;
    }
}