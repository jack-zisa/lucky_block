package dev.creoii.luckyblock.fabric;

import dev.creoii.luckyblock.LuckyBlockManager;
import dev.creoii.luckyblock.block.LuckyBlockEntity;
import dev.creoii.luckyblock.outcome.OutcomeType;
import dev.creoii.luckyblock.recipe.LuckyRecipe;
import dev.creoii.luckyblock.util.shape.ShapeType;
import dev.creoii.luckyblock.util.vec.VecProviderType;
import net.fabricmc.api.ModInitializer;

import dev.creoii.luckyblock.LuckyBlockMod;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.DataComponentType;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.profiler.Profiler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class LuckyBlockFabric implements ModInitializer {
    private static final LuckyBlockManager LUCKY_BLOCK_MANAGER = new FabricLuckyBlockManager();
    private static final BlockEntityType<LuckyBlockEntity> LUCKY_BLOCK_ENTITY = BlockEntityType.Builder.create(LuckyBlockEntity::new, LUCKY_BLOCK_MANAGER.getAllBlocks()).build(Util.getChoiceType(TypeReferences.BLOCK_ENTITY, "lucky:lucky_block"));
    private static final RecipeSerializer<LuckyRecipe> LUCKY_RECIPE_SERIALIZER = new SpecialRecipeSerializer<>(LuckyRecipe::new);
    private static final DataComponentType<Integer> LUCK_COMPONENT = new DataComponentType.Builder<Integer>().codec(Codecs.rangedInt(-100, 100)).packetCodec(PacketCodecs.VAR_INT).build();

    @Override
    public void onInitialize() {
        register();
        LuckyBlockMod.init(LUCKY_BLOCK_MANAGER, LUCKY_BLOCK_ENTITY, LUCKY_RECIPE_SERIALIZER, LUCK_COMPONENT);

        ServerTickEvents.END_SERVER_TICK.register(LuckyBlockMod.OUTCOME_MANAGER::tickDelays);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
            for (Item item : LuckyBlockMod.luckyBlockManager.getAllItems()) {
                entries.add(item);

                ItemStack positive = item.getDefaultStack();
                positive.set(LuckyBlockMod.luckComponent, 100);
                entries.add(positive);

                ItemStack negative = item.getDefaultStack();
                negative.set(LuckyBlockMod.luckComponent, -100);
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

    public void register() {
        OutcomeType.init();
        ShapeType.init();
        VecProviderType.init();
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(LuckyBlockMod.NAMESPACE, "crafting_special_lucky"), LUCKY_RECIPE_SERIALIZER);
        Registry.register(Registries.DATA_COMPONENT_TYPE, new Identifier(LuckyBlockMod.NAMESPACE, "luck"), LUCK_COMPONENT);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(LuckyBlockMod.NAMESPACE, "lucky_block"), LUCKY_BLOCK_ENTITY);
    }
}
