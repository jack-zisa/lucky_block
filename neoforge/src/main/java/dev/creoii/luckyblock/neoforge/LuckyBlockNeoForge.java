package dev.creoii.luckyblock.neoforge;

import dev.creoii.luckyblock.LuckyBlockContainer;
import dev.creoii.luckyblock.LuckyBlockManager;
import dev.creoii.luckyblock.block.LuckyBlock;
import dev.creoii.luckyblock.block.LuckyBlockEntity;
import dev.creoii.luckyblock.outcome.OutcomeType;
import dev.creoii.luckyblock.util.provider.intprovider.*;
import dev.creoii.luckyblock.util.shape.ShapeType;
import dev.creoii.luckyblock.util.vec.VecProviderType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

import dev.creoii.luckyblock.LuckyBlockMod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.Set;

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
        NeoForge.EVENT_BUS.addListener(LuckyBlockNeoForge::onItemTooltip);
    }

    private static void onRegister(RegisterEvent event) {
        event.register(Registries.BLOCK, registry -> {
            for (LuckyBlockContainer container : LUCKY_BLOCK_MANAGER.getAllContainers()) {
                BlockBehaviour.Properties blockSettings = BlockBehaviour.Properties.of().strength(container.getSettings().hardness(), container.getSettings().resistance()).mapColor(MapColor.TERRACOTTA_YELLOW).registryKey(ResourceKey.create(Registries.BLOCK, container.getId()));
                container.setBlock(new LuckyBlock(container.getId().getNamespace(), blockSettings));
                registry.register(container.getId(), container.getBlock());
            }
        });

        event.register(Registries.ITEM, registry -> {
            for (LuckyBlockContainer container : LUCKY_BLOCK_MANAGER.getAllContainers()) {
                Item.Properties itemSettings = new Item.Properties().rarity(container.getSettings().rarity()).useItemDescriptionPrefix().setId(ResourceKey.create(Registries.ITEM, container.getId()));
                container.setBlockItem(new BlockItem(container.getBlock(), itemSettings.component(LuckyBlockMod.LUCK_COMPONENT, 0).useBlockPrefixedTranslationKey()));
                registry.register(container.getId(), container.getBlockItem());
            }
        });

        event.register(Registries.BLOCK_ENTITY_TYPE, registry -> {
            luckyBlockEntity = new BlockEntityType<>(LuckyBlockEntity::new, Set.of(LUCKY_BLOCK_MANAGER.getAllBlocks()));
            Util.getChoiceType(TypeReferences.BLOCK_ENTITY, "lucky:lucky_block");
            LuckyBlockMod.setLuckyBlockEntity(luckyBlockEntity);
            registry.register(Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "lucky_block"), luckyBlockEntity);
        });

        event.register(Registries.RECIPE_SERIALIZER, registry -> {
            registry.register(Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "crafting_special_lucky"), LuckyBlockMod.LUCKY_RECIPE_SERIALIZER);
        });

        event.register(Registries.DATA_COMPONENT_TYPE, registry -> {
            registry.register(Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "luck"), LuckyBlockMod.LUCK_COMPONENT);
        });

        event.register(Registries.INT_PROVIDER_TYPE, registry -> {
            registry.register(Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "add"), AddIntProvider.CODEC);
            registry.register(Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "sub"), SubIntProvider.CODEC);
            registry.register(Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "mul"), MulIntProvider.CODEC);
            registry.register(Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "div"), DivIntProvider.CODEC);
            registry.register(Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "mod"), ModIntProvider.CODEC);
            registry.register(Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "pow"), PowIntProvider.CODEC);
            registry.register(Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "clamp"), ClampIntProvider.CODEC);
            registry.register(Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "abs"), AbsIntProvider.CODEC);
            registry.register(Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "world"), WorldIntProvider.CODEC);
        });
    }

    private static void onAddReloadListeners(AddServerReloadListenersEvent event) {
        event.addListener(Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "outcome_manager"), LuckyBlockMod.OUTCOME_MANAGER);
    }

    private static void onItemTooltip(ItemTooltipEvent event) {
        if (event.getItemStack().has(LuckyBlockMod.LUCK_COMPONENT)) {
            int luck = event.getItemStack().get(LuckyBlockMod.LUCK_COMPONENT);
            ChatFormatting formatting = luck == 0 ? ChatFormatting.GRAY : luck < 0 ? ChatFormatting.RED : ChatFormatting.GREEN;
            event.getToolTip().add(Component.translatable("lucky.item.luck", luck > 0 ? "+" + luck : luck).withStyle(formatting));
        }
    }

    private static void onBuildCreativeModTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            for (Item item : LuckyBlockMod.luckyBlockManager.getAllItems()) {
                event.accept(item);

                ItemStack lucky = item.getDefaultInstance();
                lucky.set(LuckyBlockMod.LUCK_COMPONENT, 100);
                event.accept(lucky);

                ItemStack unlucky = item.getDefaultInstance();
                unlucky.set(LuckyBlockMod.LUCK_COMPONENT, -100);
                event.accept(unlucky);
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
