package dev.creoii.luckyblock.util.stackprovider;

import com.mojang.serialization.Codec;
import dev.creoii.luckyblock.LuckyBlockRegistries;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;

public abstract class ItemStackProvider {
    public static final Codec<ItemStackProvider> TYPE_CODEC = LuckyBlockRegistries.ITEM_STACK_PROVIDER_TYPE.getCodec().dispatch(ItemStackProvider::getType, ItemStackProviderType::codec);

    public static SimpleItemStackProvider of(ItemStack stack) {
        return new SimpleItemStackProvider(stack);
    }

    public static SimpleItemStackProvider of(Item item) {
        return new SimpleItemStackProvider(item.getDefaultStack());
    }

    protected abstract ItemStackProviderType<?> getType();

    public abstract ItemStack get(Outcome.Context<?> context, Random random);
}
