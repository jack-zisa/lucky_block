package dev.creoii.luckyblock.neoforge;

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
        event.addListener((synchronizer, manager, prepareProfiler, applyProfiler, prepareExecutor, applyExecutor) -> {
            return LuckyBlockMod.OUTCOME_MANAGER.reload(synchronizer, manager, prepareProfiler, applyProfiler, prepareExecutor, applyExecutor);
        });
    }

    private static void onBuildCreativeModTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == ItemGroups.BUILDING_BLOCKS) {
            event.add(LuckyBlockMod.TEST_LUCKY_BLOCK_ITEM);

            ItemStack positive = new ItemStack(LuckyBlockMod.TEST_LUCKY_BLOCK_ITEM);
            positive.set(LuckyBlockMod.LUCK, 100);
            event.add(positive);

            ItemStack negative = new ItemStack(LuckyBlockMod.TEST_LUCKY_BLOCK_ITEM);
            negative.set(LuckyBlockMod.LUCK, -100);
            event.add(negative);
        }
    }
}
