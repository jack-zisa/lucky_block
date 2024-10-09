package dev.creoii.luckyblock.luckyblock;

import dev.creoii.luckyblock.LuckyBlockMod;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class LuckyBlockItem extends BlockItem {
    public LuckyBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        if (stack.contains(LuckyBlockMod.LUCK)) {
            int luck = stack.get(LuckyBlockMod.LUCK);
            Formatting formatting = luck == 0 ? Formatting.GRAY : luck < 0 ? Formatting.RED : Formatting.GREEN;
            tooltip.add(Text.translatable("lucky.item.luck", luck).formatted(formatting));
        }
    }
}
