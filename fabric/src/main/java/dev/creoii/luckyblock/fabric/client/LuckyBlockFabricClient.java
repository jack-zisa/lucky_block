package dev.creoii.luckyblock.fabric.client;

import dev.creoii.luckyblock.LuckyBlockMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class LuckyBlockFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, list) -> {
            if (stack.contains(LuckyBlockMod.LUCK_COMPONENT)) {
                int luck = stack.get(LuckyBlockMod.LUCK_COMPONENT);
                Formatting formatting = luck == 0 ? Formatting.GRAY : luck < 0 ? Formatting.RED : Formatting.GREEN;
                list.add(Text.translatable("lucky.item.luck", luck > 0 ? "+" + luck : luck).formatted(formatting));
            }
        });
    }
}
