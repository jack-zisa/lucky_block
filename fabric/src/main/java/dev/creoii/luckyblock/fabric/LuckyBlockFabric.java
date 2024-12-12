package dev.creoii.luckyblock.fabric;

import dev.creoii.luckyblock.LuckyBlockManager;
import dev.creoii.luckyblock.block.LuckyBlockEntity;
import dev.creoii.luckyblock.outcome.OutcomeType;
import dev.creoii.luckyblock.util.colorprovider.ColorProviderType;
import dev.creoii.luckyblock.util.function.FunctionType;
import dev.creoii.luckyblock.util.function.target.FunctionTargetType;
import dev.creoii.luckyblock.util.shape.ShapeType;
import dev.creoii.luckyblock.util.stackprovider.ItemStackProviderType;
import dev.creoii.luckyblock.util.vecprovider.VecProviderType;
import net.fabricmc.api.ModInitializer;

import dev.creoii.luckyblock.LuckyBlockMod;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class LuckyBlockFabric implements ModInitializer {
    private static final LuckyBlockManager LUCKY_BLOCK_MANAGER = new FabricLuckyBlockManager();
    private static final BlockEntityType<LuckyBlockEntity> luckyBlockEntity = FabricBlockEntityTypeBuilder.create(LuckyBlockEntity::new, LUCKY_BLOCK_MANAGER.getAllBlocks()).build(Util.getChoiceType(TypeReferences.BLOCK_ENTITY, "lucky:lucky_block"));

    @Override
    public void onInitialize() {
        ItemStackProviderType.init();
        VecProviderType.init();
        ColorProviderType.init();
        OutcomeType.init();
        FunctionType.init();
        FunctionTargetType.init();
        ShapeType.init();

        Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(LuckyBlockMod.NAMESPACE, "crafting_special_lucky"), LuckyBlockMod.LUCKY_RECIPE_SERIALIZER);
        Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(LuckyBlockMod.NAMESPACE, "luck"), LuckyBlockMod.LUCK_COMPONENT);
        LuckyBlockMod.setLuckyBlockEntity(luckyBlockEntity);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(LuckyBlockMod.NAMESPACE, "lucky_block"), luckyBlockEntity);

        LuckyBlockMod.init(LUCKY_BLOCK_MANAGER);

        ServerTickEvents.END_SERVER_TICK.register(LuckyBlockMod.OUTCOME_MANAGER::tickDelays);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
            for (Item item : LuckyBlockMod.luckyBlockManager.getAllItems()) {
                entries.add(item);

                ItemStack positive = item.getDefaultStack();
                positive.set(LuckyBlockMod.LUCK_COMPONENT, 100);
                entries.add(positive);

                ItemStack negative = item.getDefaultStack();
                negative.set(LuckyBlockMod.LUCK_COMPONENT, -100);
                entries.add(negative);
            }
        });

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new IdentifiableResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return Identifier.of(LuckyBlockMod.NAMESPACE, "outcomes");
            }

            @Override
            public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Executor prepareExecutor, Executor applyExecutor) {
                return LuckyBlockMod.OUTCOME_MANAGER.reload(synchronizer, manager, prepareExecutor, applyExecutor);
            }
        });
    }
}
