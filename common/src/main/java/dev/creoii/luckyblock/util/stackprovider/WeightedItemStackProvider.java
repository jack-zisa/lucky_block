package dev.creoii.luckyblock.util.stackprovider;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.random.Random;

public class WeightedItemStackProvider extends ItemStackProvider {
    public static final MapCodec<WeightedItemStackProvider> CODEC = DataPool.createCodec(ItemStack.CODEC).comapFlatMap(WeightedItemStackProvider::wrap, provider -> {
        return provider.stacks;
    }).fieldOf("entries");
    private final DataPool<ItemStack> stacks;

    private static DataResult<WeightedItemStackProvider> wrap(DataPool<ItemStack> stacks) {
        return stacks.isEmpty() ? DataResult.error(() -> {
            return "WeightedStackProvider with no stacks";
        }) : DataResult.success(new WeightedItemStackProvider(stacks));
    }

    public WeightedItemStackProvider(DataPool<ItemStack> stacks) {
        this.stacks = stacks;
    }

    public WeightedItemStackProvider(DataPool.Builder<ItemStack> stacks) {
        this(stacks.build());
    }

    protected ItemStackProviderType<?> getType() {
        return ItemStackProviderType.WEIGHTED_STACK_PROVIDER;
    }

    @Override
    public ItemStack get(Outcome.Context<?> context, Random random) {
        return stacks.getDataOrEmpty(random).orElseThrow(IllegalStateException::new);
    }
}
