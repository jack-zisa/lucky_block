package dev.creoii.luckyblock.fabric;

import net.fabricmc.api.ModInitializer;

import dev.creoii.luckyblock.LuckyBlockMod;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class LuckyBlockFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        LuckyBlockMod.init();

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
            for (Item item : LuckyBlockMod.LUCKY_BLOCK_MANAGER.getAllItems()) {
                entries.add(item);

                ItemStack positive = item.getDefaultStack();
                positive.set(LuckyBlockMod.LUCK, 100);
                entries.add(positive);

                ItemStack negative = item.getDefaultStack();
                negative.set(LuckyBlockMod.LUCK, -100);
                entries.add(negative);
            }
        });

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new IdentifiableResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier(LuckyBlockMod.NAMESPACE, "outcomes");
            }

            @Override
            public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
                return LuckyBlockMod.OUTCOME_MANAGER.reload(synchronizer, manager, prepareProfiler, applyProfiler, prepareExecutor, applyExecutor);
            }
        });
    }
}
