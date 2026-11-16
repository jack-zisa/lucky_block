package dev.creoii.luckyblock.recipe;

import dev.creoii.luckyblock.LuckyBlockContainer;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.block.LuckyBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.Set;
import java.util.stream.Collectors;

public class LuckyRecipe extends CustomRecipe {
    public LuckyRecipe() {
        super();
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        Set<ItemStack> luckyBlocks = input.items().stream().filter(stack -> stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof LuckyBlock).collect(Collectors.toSet());
        if (luckyBlocks.size() == 1) {
            LuckyBlockContainer container = LuckyBlockMod.luckyBlockManager.getContainer(luckyBlocks.iterator().next().getItem().arch$registryName().getNamespace());
            Set<ItemStack> luckItems = input.items().stream().filter(stack -> container.getItemLuck().containsKey(stack.getItem())).collect(Collectors.toSet());
            return !luckItems.isEmpty();
        }
        return false;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        ItemStack luckyBlock = input.items().stream().filter(stack -> stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof LuckyBlock).collect(Collectors.toSet()).iterator().next();
        LuckyBlockContainer container = LuckyBlockMod.luckyBlockManager.getContainer(luckyBlock.getItem().arch$registryName().getNamespace());

        ItemStack result = luckyBlock.copy();
        Integer luck = luckyBlock.getOrDefault(LuckyBlockMod.LUCK_COMPONENT, 0);
        if (container != null) {
            for (ItemStack stack : input.items()) {
                luck = Math.clamp(luck + container.getItemLuckValue(stack.getItem()), -100, 100);
            }
            result.set(LuckyBlockMod.LUCK_COMPONENT, luck);
        }
        return result;
    }

    /*@Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }*/

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return LuckyBlockMod.LUCKY_RECIPE_SERIALIZER;
    }
}
