package dev.creoii.luckyblock.util.stackprovider;

import com.mojang.serialization.MapCodec;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;

public class SimpleItemStackProvider extends ItemStackProvider {
    public static final MapCodec<SimpleItemStackProvider> CODEC = ItemStack.CODEC.fieldOf("stack").xmap(SimpleItemStackProvider::new, provider -> provider.stack);
    private final ItemStack stack;

    public SimpleItemStackProvider(ItemStack stack) {
        this.stack = stack;
    }

    protected ItemStackProviderType<?> getType() {
        return ItemStackProviderType.SIMPLE_STACK_PROVIDER;
    }

    public ItemStack get(Random random) {
        return stack;
    }
}
