package dev.creoii.luckyblock.util.function;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * This utility class contains wrapper {@link Codec} for all used objects in the Lucky Block mod, including, but not limited to, {@link ItemStack}, {@link net.minecraft.block.BlockState}, and {@link net.minecraft.entity.Entity}.
 */
public class FunctionObjectCodecs {
    public static final Codec<ItemStack> ITEM_STACK = Codec.either(Identifier.CODEC, ItemStack.CODEC).xmap(either -> {
        return either.map(identifier -> Registries.ITEM.get(identifier).getDefaultStack(), java.util.function.Function.identity());
    }, Either::right);

    public static final Codec<ItemStackWrapper> INLINE_ITEM_STACK = Codec.lazyInitialized(() -> RecordCodecBuilder.create(instance -> instance.group(
            ITEM_STACK.fieldOf("stack").forGetter(ItemStackWrapper::stack),
            Function.CODEC.listOf().fieldOf("functions").forGetter(ItemStackWrapper::functions)
    ).apply(instance, ItemStackWrapper::new)));

    public static final Codec<ItemStackWrapper> ITEM_STACK_WRAPPER = Codec.either(Identifier.CODEC, INLINE_ITEM_STACK).xmap(either -> {
        return either.map(identifier -> ItemStackWrapper.fromStack(Registries.ITEM.get(identifier).getDefaultStack()), java.util.function.Function.identity());
    }, Either::right);

    public record ItemStackWrapper(ItemStack stack, List<Function<?>> functions) {
        public static ItemStackWrapper fromStack(ItemStack stack) {
            return new ItemStackWrapper(stack, List.of());
        }

        public ItemStack toStack(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
            functions.forEach(function -> function.apply(outcome, context));
            return stack;
        }
    }
}
