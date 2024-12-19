package dev.creoii.luckyblock;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import dev.creoii.luckyblock.block.LuckyBlockEntity;
import dev.creoii.luckyblock.outcome.OutcomeManager;
import dev.creoii.luckyblock.outcome.OutcomeType;
import dev.creoii.luckyblock.recipe.LuckyRecipe;
import dev.creoii.luckyblock.util.resource.AddonAtlasSource;
import dev.creoii.luckyblock.util.vec.VecProviderType;
import dev.creoii.luckyblock.util.shape.ShapeType;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.texture.atlas.AtlasSourceManager;
import net.minecraft.client.texture.atlas.AtlasSourceType;
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

    public static final RegistryKey<Registry<ShapeType>> SHAPE_TYPES_KEY = RegistryKey.ofRegistry(Identifier.of(NAMESPACE, "shape_types"));
    public static final Registry<ShapeType> SHAPE_TYPES = new SimpleDefaultedRegistry<>("lucky:empty", SHAPE_TYPES_KEY, Lifecycle.stable(), false);

    public static final RegistryKey<Registry<VecProviderType<?>>> POS_PROVIDER_TYPES_KEY = RegistryKey.ofRegistry(Identifier.of(NAMESPACE, "pos_provider_types"));
    public static final Registry<VecProviderType<?>> POS_PROVIDER_TYPES = new SimpleDefaultedRegistry<>("lucky:zero", POS_PROVIDER_TYPES_KEY, Lifecycle.stable(), false);

    public static final AtlasSourceType ADDON_ATLAS_SOURCE = new AtlasSourceType(AddonAtlasSource.CODEC);

    public static BlockEntityType<LuckyBlockEntity> LUCKY_BLOCK_ENTITY;

    public static void init(LuckyBlockManager luckyBlockManager) {
        AtlasSourceManager.SOURCE_TYPE_BY_ID.put(Identifier.of(NAMESPACE, "addon"), ADDON_ATLAS_SOURCE);
        LuckyBlockMod.luckyBlockManager = luckyBlockManager;
    }

    public static void setLuckyBlockEntity(BlockEntityType<LuckyBlockEntity> luckyBlockEntity) {
        LUCKY_BLOCK_ENTITY = luckyBlockEntity;
    }
}
