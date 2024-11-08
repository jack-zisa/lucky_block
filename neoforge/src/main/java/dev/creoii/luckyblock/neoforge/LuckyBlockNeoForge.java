package dev.creoii.luckyblock.neoforge;

import dev.creoii.luckyblock.LuckyBlockManager;
import dev.creoii.luckyblock.block.LuckyBlockEntity;
import dev.creoii.luckyblock.outcome.OutcomeType;
import dev.creoii.luckyblock.recipe.LuckyRecipe;
import dev.creoii.luckyblock.util.shape.ShapeType;
import dev.creoii.luckyblock.util.vec.VecProviderType;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentType;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
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
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(LuckyBlockMod.NAMESPACE)
public final class LuckyBlockNeoForge {
    private static final LuckyBlockManager LUCKY_BLOCK_MANAGER = new NeoForgeLuckyBlockManager();

    @SuppressWarnings("unchecked")
    public LuckyBlockNeoForge(IEventBus modBus) {
        modBus.addListener(LuckyBlockNeoForge::onRegister);

        register();
        LuckyBlockMod.init(LUCKY_BLOCK_MANAGER, (BlockEntityType<LuckyBlockEntity>) Registries.BLOCK_ENTITY_TYPE.get(Identifier.of(LuckyBlockMod.NAMESPACE, "lucky_block")), (RecipeSerializer<LuckyRecipe>) Registries.RECIPE_SERIALIZER.get(Identifier.of(LuckyBlockMod.NAMESPACE, "crafting_special_lucky")), (ComponentType<Integer>) Registries.DATA_COMPONENT_TYPE.get(Identifier.of(LuckyBlockMod.NAMESPACE, "luck")));

        NeoForge.EVENT_BUS.addListener(LuckyBlockNeoForge::onAddReloadListeners);
        modBus.addListener(LuckyBlockNeoForge::onBuildCreativeModTabContents);
    }

    private static void onRegister(RegisterEvent event) {
        event.register(RegistryKeys.BLOCK_ENTITY_TYPE, registry -> {
            registry.register(Identifier.of(LuckyBlockMod.NAMESPACE, "lucky_block"), BlockEntityType.Builder.create(LuckyBlockEntity::new, LUCKY_BLOCK_MANAGER.getAllBlocks()).build(Util.getChoiceType(TypeReferences.BLOCK_ENTITY, "lucky:lucky_block")));
        });

        event.register(RegistryKeys.RECIPE_SERIALIZER, registry -> {
            registry.register(Identifier.of(LuckyBlockMod.NAMESPACE, "crafting_special_lucky"), new SpecialRecipeSerializer<>(LuckyRecipe::new));
        });

        event.register(RegistryKeys.DATA_COMPONENT_TYPE, registry -> {
            registry.register(Identifier.of(LuckyBlockMod.NAMESPACE, "luck"), new ComponentType.Builder<Integer>().codec(Codecs.rangedInt(-100, 100)).packetCodec(PacketCodecs.VAR_INT).build());
        });
    }

    private static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(LuckyBlockMod.OUTCOME_MANAGER);
    }

    private static void onBuildCreativeModTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == ItemGroups.BUILDING_BLOCKS) {
            for (Item item : LuckyBlockMod.luckyBlockManager.getAllItems()) {
                event.add(item);

                ItemStack positive = item.getDefaultStack();
                positive.set(LuckyBlockMod.luckComponent, 100);
                event.add(positive);

                ItemStack negative = item.getDefaultStack();
                negative.set(LuckyBlockMod.luckComponent, -100);
                event.add(negative);
            }
        }
    }

    public void register() {
        OutcomeType.init();
        ShapeType.init();
        VecProviderType.init();
    }
}
