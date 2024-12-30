package dev.creoii.luckyblock;

import com.mojang.logging.LogUtils;
import dev.creoii.luckyblock.block.LuckyBlockEntity;
import dev.creoii.luckyblock.outcome.OutcomeManager;
import dev.creoii.luckyblock.recipe.LuckyRecipe;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.dynamic.Codecs;
import org.slf4j.Logger;

public final class LuckyBlockMod {
    public static final String NAMESPACE = "lucky";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static LuckyBlockManager luckyBlockManager;
    public static final OutcomeManager OUTCOME_MANAGER = new OutcomeManager();

    public static final RecipeSerializer<LuckyRecipe> LUCKY_RECIPE_SERIALIZER = new SpecialCraftingRecipe.SpecialRecipeSerializer<>(LuckyRecipe::new);

    public static final ComponentType<Integer> LUCK_COMPONENT = new ComponentType.Builder<Integer>().codec(Codecs.rangedInt(-100, 100)).packetCodec(PacketCodecs.VAR_INT).build();

    public static BlockEntityType<LuckyBlockEntity> LUCKY_BLOCK_ENTITY;

    public static void init(LuckyBlockManager luckyBlockManager) {
        LuckyBlockMod.luckyBlockManager = luckyBlockManager;
    }

    public static void setLuckyBlockEntity(BlockEntityType<LuckyBlockEntity> luckyBlockEntity) {
        LUCKY_BLOCK_ENTITY = luckyBlockEntity;
    }
}
