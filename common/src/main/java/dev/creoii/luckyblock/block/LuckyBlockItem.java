package dev.creoii.luckyblock.block;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LuckyBlockItem extends BlockItem {
    public LuckyBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    public static int getLuck(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        return nbtCompound != null ? nbtCompound.getInt("luck") : 0;
    }

    public static void setLuck(ItemStack stack, int luck) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putInt("luck", luck);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        int luck = getLuck(stack);
        Formatting formatting = luck == 0 ? Formatting.GRAY : luck < 0 ? Formatting.RED : Formatting.GREEN;
        tooltip.add(Text.translatable("lucky.item.luck", luck > 0 ? "+" + luck : luck).formatted(formatting));
    }
}
