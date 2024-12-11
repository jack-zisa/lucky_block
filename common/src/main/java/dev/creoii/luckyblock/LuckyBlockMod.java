package dev.creoii.luckyblock;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import dev.creoii.luckyblock.block.LuckyBlockEntity;
import dev.creoii.luckyblock.outcome.OutcomeManager;
import dev.creoii.luckyblock.outcome.OutcomeType;
import dev.creoii.luckyblock.recipe.LuckyRecipe;
import dev.creoii.luckyblock.util.colorprovider.ColorProviderType;
import dev.creoii.luckyblock.util.function.FunctionType;
import dev.creoii.luckyblock.util.function.target.FunctionTargetType;
import dev.creoii.luckyblock.util.stackprovider.ItemStackProviderType;
import dev.creoii.luckyblock.util.vec.VecProviderType;
import dev.creoii.luckyblock.util.shape.ShapeType;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleDefaultedRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import org.slf4j.Logger;

public final class LuckyBlockMod {
    public static final String NAMESPACE = "lucky";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static LuckyBlockManager luckyBlockManager;
    public static final OutcomeManager OUTCOME_MANAGER = new OutcomeManager();

    public static final RecipeSerializer<LuckyRecipe> LUCKY_RECIPE_SERIALIZER = new SpecialCraftingRecipe.SpecialRecipeSerializer<>(LuckyRecipe::new);

    public static final ComponentType<Integer> LUCK_COMPONENT = new ComponentType.Builder<Integer>().codec(Codecs.rangedInt(-100, 100)).packetCodec(PacketCodecs.VAR_INT).build();

    public static final RegistryKey<Registry<OutcomeType>> OUTCOME_TYPES_KEY = RegistryKey.ofRegistry(Identifier.of(NAMESPACE, "outcome_types"));
    public static final Registry<OutcomeType> OUTCOME_TYPES = new SimpleDefaultedRegistry<>("lucky:none", OUTCOME_TYPES_KEY, Lifecycle.stable(), false);

    public static final RegistryKey<Registry<ItemStackProviderType<?>>> ITEM_STACK_PROVIDER_TYPE_KEY = RegistryKey.ofRegistry(Identifier.of(NAMESPACE, "item_stack_provider_type"));
    public static final Registry<ItemStackProviderType<?>> ITEM_STACK_PROVIDER_TYPE = new SimpleDefaultedRegistry<>("lucky:simple_stack_provider", ITEM_STACK_PROVIDER_TYPE_KEY, Lifecycle.stable(), false);

    public static final RegistryKey<Registry<ColorProviderType<?>>> COLOR_PROVIDER_TYPE_KEY = RegistryKey.ofRegistry(Identifier.of(NAMESPACE, "color_provider_type"));
    public static final Registry<ColorProviderType<?>> COLOR_PROVIDER_TYPE = new SimpleDefaultedRegistry<>("lucky:simple_color_provider", COLOR_PROVIDER_TYPE_KEY, Lifecycle.stable(), false);

    public static final RegistryKey<Registry<FunctionType>> FUNCTION_TYPES_KEY = RegistryKey.ofRegistry(Identifier.of(NAMESPACE, "function_types"));
    public static final Registry<FunctionType> FUNCTION_TYPES = new SimpleDefaultedRegistry<>("lucky:empty", FUNCTION_TYPES_KEY, Lifecycle.stable(), false);

    public static final RegistryKey<Registry<FunctionTargetType>> FUNCTION_TARGET_TYPES_KEY = RegistryKey.ofRegistry(Identifier.of(NAMESPACE, "function_target_types"));
    public static final Registry<FunctionTargetType> FUNCTION_TARGET_TYPES = new SimpleDefaultedRegistry<>("lucky:default", FUNCTION_TARGET_TYPES_KEY, Lifecycle.stable(), false);

    public static final RegistryKey<Registry<ShapeType>> SHAPE_TYPES_KEY = RegistryKey.ofRegistry(Identifier.of(NAMESPACE, "shape_types"));
    public static final Registry<ShapeType> SHAPE_TYPES = new SimpleDefaultedRegistry<>("lucky:empty", SHAPE_TYPES_KEY, Lifecycle.stable(), false);

    public static final RegistryKey<Registry<VecProviderType<?>>> POS_PROVIDER_TYPES_KEY = RegistryKey.ofRegistry(Identifier.of(NAMESPACE, "pos_provider_types"));
    public static final Registry<VecProviderType<?>> POS_PROVIDER_TYPES = new SimpleDefaultedRegistry<>("lucky:zero", POS_PROVIDER_TYPES_KEY, Lifecycle.stable(), false);

    public static BlockEntityType<LuckyBlockEntity> LUCKY_BLOCK_ENTITY;

    public static void init(LuckyBlockManager luckyBlockManager) {
        LuckyBlockMod.luckyBlockManager = luckyBlockManager;
    }

    public static void setLuckyBlockEntity(BlockEntityType<LuckyBlockEntity> luckyBlockEntity) {
        LUCKY_BLOCK_ENTITY = luckyBlockEntity;
    }
}
