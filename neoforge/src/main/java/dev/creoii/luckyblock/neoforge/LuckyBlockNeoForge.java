package dev.creoii.luckyblock.neoforge;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.neoforged.fml.common.Mod;

import dev.creoii.luckyblock.LuckyBlockMod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@Mod(LuckyBlockMod.NAMESPACE)
public final class LuckyBlockNeoForge {
    public LuckyBlockNeoForge() {
        LuckyBlockMod.init();
        NeoForge.EVENT_BUS.addListener(LuckyBlockNeoForge::onAddReloadListeners);
        NeoForge.EVENT_BUS.addListener(LuckyBlockNeoForge::onBuildCreativeModTabContents);
    }

    private static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(LuckyBlockMod.OUTCOME_MANAGER);
    }

    private static void onBuildCreativeModTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == ItemGroups.BUILDING_BLOCKS) {
            for (Item item : LuckyBlockMod.LUCKY_BLOCK_MANAGER.getAllItems()) {
                event.add(item);

                ItemStack positive = item.getDefaultStack();
                positive.set(LuckyBlockMod.LUCK, 100);
                event.add(positive);

                ItemStack negative = item.getDefaultStack();
                negative.set(LuckyBlockMod.LUCK, -100);
                event.add(negative);
            }
        }
    }
}
