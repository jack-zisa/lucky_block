package dev.creoii.luckyblock.util.function;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.function.wrapper.ItemStackWrapper;
import dev.creoii.luckyblock.util.stackprovider.ItemStackProvider;
import dev.creoii.luckyblock.util.stackprovider.SimpleItemStackProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

/**
 * This utility class contains wrapper {@link Codec} for all used objects in the Lucky Block mod, including, but not limited to, {@link ItemStack}, {@link net.minecraft.block.BlockState}, and {@link net.minecraft.entity.Entity}.
 */
public class FunctionObjectCodecs {
    public static final Codec<ItemStackProvider> ITEM_STACK = Codec.either(Identifier.CODEC, ItemStackProvider.TYPE_CODEC).xmap(either -> {
        return either.map(identifier -> SimpleItemStackProvider.of(Registries.ITEM.get(identifier).getDefaultStack()), java.util.function.Function.identity());
    }, Either::right);

    public static final Codec<ItemStackWrapper> INLINE_ITEM_STACK = Codec.lazyInitialized(() -> RecordCodecBuilder.create(instance -> instance.group(
            ITEM_STACK.fieldOf("stack_provider").forGetter(ItemStackWrapper::stackProvider),
            Function.CODEC.listOf().fieldOf("functions").forGetter(ItemStackWrapper::functions)
    ).apply(instance, ItemStackWrapper::new)));

    public static final Codec<ItemStackWrapper> ITEM_STACK_WRAPPER = Codec.either(Identifier.CODEC, INLINE_ITEM_STACK).xmap(either -> {
        return either.map(identifier -> ItemStackWrapper.fromStack(Registries.ITEM.get(identifier).getDefaultStack()), java.util.function.Function.identity());
    }, Either::right);
}
