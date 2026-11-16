package dev.creoii.luckyblock;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import dev.creoii.luckyblock.block.LuckyBlockEntity;
import dev.creoii.luckyblock.outcome.OutcomeManager;
import dev.creoii.luckyblock.outcome.OutcomeType;
import dev.creoii.luckyblock.recipe.LuckyRecipe;
import dev.creoii.luckyblock.util.provider.booleanprovider.BooleanProviderType;
import dev.creoii.luckyblock.util.provider.stringprovider.StringProviderType;
import dev.creoii.luckyblock.util.vec.VecProviderType;
import dev.creoii.luckyblock.util.shape.ShapeType;
import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.slf4j.Logger;

public final class LuckyBlockMod {
    public static final String NAMESPACE = "lucky";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static LuckyBlockManager luckyBlockManager;
    public static final OutcomeManager OUTCOME_MANAGER = new OutcomeManager();

    public static final RecipeSerializer<LuckyRecipe> LUCKY_RECIPE_SERIALIZER = new RecipeSerializer<>(LuckyRecipe::new);

    public static final DataComponentType<Integer> LUCK_COMPONENT = new DataComponentType.Builder<Integer>().persistent(Codec.intRange(-100, 100)).networkSynchronized(ByteBufCodecs.VAR_INT).build();

    public static final ResourceKey<Registry<OutcomeType>> OUTCOME_TYPES_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(NAMESPACE, "outcome_types"));
    public static final Registry<OutcomeType> OUTCOME_TYPES = new DefaultedMappedRegistry<>("lucky:none", OUTCOME_TYPES_KEY, Lifecycle.stable(), false);

    public static final ResourceKey<Registry<ShapeType>> SHAPE_TYPES_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(NAMESPACE, "shape_types"));
    public static final Registry<ShapeType> SHAPE_TYPES = new DefaultedMappedRegistry<>("lucky:empty", SHAPE_TYPES_KEY, Lifecycle.stable(), false);

    public static final ResourceKey<Registry<VecProviderType<?>>> POS_PROVIDER_TYPES_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(NAMESPACE, "pos_provider_types"));
    public static final Registry<VecProviderType<?>> POS_PROVIDER_TYPES = new DefaultedMappedRegistry<>("lucky:zero", POS_PROVIDER_TYPES_KEY, Lifecycle.stable(), false);

    public static final ResourceKey<Registry<StringProviderType<?>>> STRING_PROVIDER_TYPES_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(NAMESPACE, "string_provider_types"));
    public static final Registry<StringProviderType<?>> STRING_PROVIDER_TYPES = new DefaultedMappedRegistry<>("lucky:empty", STRING_PROVIDER_TYPES_KEY, Lifecycle.stable(), false);

    public static final ResourceKey<Registry<BooleanProviderType<?>>> BOOLEAN_PROVIDER_TYPES_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(NAMESPACE, "boolean_provider_types"));
    public static final Registry<BooleanProviderType<?>> BOOLEAN_PROVIDER_TYPES = new DefaultedMappedRegistry<>("lucky:true", BOOLEAN_PROVIDER_TYPES_KEY, Lifecycle.stable(), false);

    public static BlockEntityType<LuckyBlockEntity> LUCKY_BLOCK_ENTITY;

    public static void init(LuckyBlockManager luckyBlockManager) {
        LuckyBlockMod.luckyBlockManager = luckyBlockManager;
    }

    public static void setLuckyBlockEntity(BlockEntityType<LuckyBlockEntity> luckyBlockEntity) {
        LUCKY_BLOCK_ENTITY = luckyBlockEntity;
    }
}
