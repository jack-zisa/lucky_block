package dev.creoii.luckyblock.neoforge;

import dev.creoii.luckyblock.LuckyBlockContainer;
import dev.creoii.luckyblock.LuckyBlockManager;
import dev.creoii.luckyblock.block.LuckyBlock;
import dev.creoii.luckyblock.block.LuckyBlockEntity;
import dev.creoii.luckyblock.outcome.OutcomeType;
import dev.creoii.luckyblock.recipe.LuckyRecipe;
import dev.creoii.luckyblock.util.shape.ShapeType;
import dev.creoii.luckyblock.util.vec.VecProviderType;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.DataComponentType;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

import dev.creoii.luckyblock.LuckyBlockMod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(LuckyBlockMod.NAMESPACE)
public final class LuckyBlockNeoForge {
    private static final LuckyBlockManager LUCKY_BLOCK_MANAGER = new NeoForgeLuckyBlockManager();
    private static BlockEntityType<LuckyBlockEntity> luckyBlockEntity;

    public LuckyBlockNeoForge(IEventBus modBus) {
        modBus.addListener(LuckyBlockNeoForge::onRegister);

        register();
        LuckyBlockMod.init(LUCKY_BLOCK_MANAGER);

        NeoForge.EVENT_BUS.addListener(LuckyBlockNeoForge::onAddReloadListeners);
        modBus.addListener(LuckyBlockNeoForge::onBuildCreativeModTabContents);
        NeoForge.EVENT_BUS.addListener(LuckyBlockNeoForge::onServerTick);
    }

    private static void onRegister(RegisterEvent event) {
        event.register(RegistryKeys.BLOCK, registry -> {
            for (LuckyBlockContainer container : LUCKY_BLOCK_MANAGER.getAllContainers()) {
                AbstractBlock.Settings blockSettings = AbstractBlock.Settings.create().hardness(container.getSettings().hardness()).resistance(container.getSettings().resistance()).mapColor(MapColor.TERRACOTTA_YELLOW);
                container.setBlock(new LuckyBlock(container.getId().getNamespace(), blockSettings));
                registry.register(container.getId(), container.getBlock());
            }
        });

        event.register(RegistryKeys.ITEM, registry -> {
            for (LuckyBlockContainer container : LUCKY_BLOCK_MANAGER.getAllContainers()) {
                Item.Settings itemSettings = new Item.Settings().rarity(container.getSettings().rarity());
                container.setBlockItem(new BlockItem(container.getBlock(), itemSettings.component(LuckyBlockMod.LUCK_COMPONENT, 0)));
                registry.register(container.getId(), container.getBlockItem());
            }
        });

        event.register(RegistryKeys.BLOCK_ENTITY_TYPE, registry -> {
            luckyBlockEntity = BlockEntityType.Builder.create(LuckyBlockEntity::new, LUCKY_BLOCK_MANAGER.getAllBlocks()).build(Util.getChoiceType(TypeReferences.BLOCK_ENTITY, "lucky:lucky_block"));
            LuckyBlockMod.setLuckyBlockEntity(luckyBlockEntity);
            registry.register(new Identifier(LuckyBlockMod.NAMESPACE, "lucky_block"), luckyBlockEntity);
        });

        event.register(RegistryKeys.RECIPE_SERIALIZER, registry -> {
            registry.register(new Identifier(LuckyBlockMod.NAMESPACE, "crafting_special_lucky"), LuckyBlockMod.LUCKY_RECIPE_SERIALIZER);
        });

        event.register(RegistryKeys.DATA_COMPONENT_TYPE, registry -> {
            registry.register(new Identifier(LuckyBlockMod.NAMESPACE, "luck"), LuckyBlockMod.LUCK_COMPONENT);
        });    }

    private static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(LuckyBlockMod.OUTCOME_MANAGER);
    }

    private static void onBuildCreativeModTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == ItemGroups.FUNCTIONAL) {
            for (Item item : LuckyBlockMod.luckyBlockManager.getAllItems()) {
                event.add(item);

                ItemStack positive = item.getDefaultStack().copy();
                positive.set(LuckyBlockMod.LUCK_COMPONENT, 100);
                event.add(positive);

                ItemStack negative = item.getDefaultStack().copy();
                negative.set(LuckyBlockMod.LUCK_COMPONENT, -100);
                event.add(negative);
            }
        }
    }

    private static void onServerTick(ServerTickEvent.Post event) {
        LuckyBlockMod.OUTCOME_MANAGER.tickDelays(event.getServer());
    }

    public void register() {
        OutcomeType.init();
        ShapeType.init();
        VecProviderType.init();
    }
}
