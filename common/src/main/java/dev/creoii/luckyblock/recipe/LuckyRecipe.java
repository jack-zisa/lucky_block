package dev.creoii.luckyblock.recipe;

import dev.creoii.luckyblock.LuckyBlockContainer;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.block.LuckyBlock;
import dev.creoii.luckyblock.block.LuckyBlockItem;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.*;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.world.World;

import java.util.Set;
import java.util.stream.Collectors;

public class LuckyRecipe extends SpecialCraftingRecipe {
    public LuckyRecipe(CraftingRecipeCategory category) {
        super(category);
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        Set<ItemStack> luckyBlocks = inventory.getHeldStacks().stream().filter(stack -> stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof LuckyBlock).collect(Collectors.toSet());
        if (luckyBlocks.size() == 1) {
            LuckyBlockContainer container = LuckyBlockMod.luckyBlockManager.getContainer(Registries.ITEM.getId(luckyBlocks.iterator().next().getItem()).getNamespace());
            Set<ItemStack> luckItems = inventory.getHeldStacks().stream().filter(stack -> container.getItemLuck().containsKey(stack.getItem())).collect(Collectors.toSet());
            return !luckItems.isEmpty();
        }
        return false;
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        ItemStack luckyBlock = inventory.getHeldStacks().stream().filter(stack -> stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof LuckyBlock).collect(Collectors.toSet()).iterator().next();
        LuckyBlockContainer container = LuckyBlockMod.luckyBlockManager.getContainer(Registries.ITEM.getId(luckyBlock.getItem()).getNamespace());

        ItemStack result = luckyBlock.copy();
        int luck = LuckyBlockItem.getLuck(luckyBlock);
        if (container != null) {
            for (ItemStack stack : inventory.getHeldStacks()) {
                luck = Math.clamp(luck + container.getItemLuckValue(stack.getItem()), -100, 100);
            }
            LuckyBlockItem.setLuck(result, luck);
        }
        return result;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return LuckyBlockMod.LUCKY_RECIPE_SERIALIZER;
    }
}
