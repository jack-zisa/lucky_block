package dev.creoii.luckyblock;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import dev.creoii.luckyblock.block.LuckyBlock;
import dev.creoii.luckyblock.outcome.*;
import dev.creoii.luckyblock.util.position.VecProviderType;
import dev.creoii.luckyblock.util.shape.ShapeType;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.component.DataComponentType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.dynamic.Codecs;
import org.slf4j.Logger;

/**
 * Todo:
 * <ul>
 *     <li>Outcome referencing</li>
 *     <li>.mcfunction Outcome</li>
 *     <li>ensure game events are properly thrown in outcomes</li>
 *     <li>Hand/Armor Items do not work</li>
 *     <li>Collar colors are all the same</li>
 *     <li>Explosion outcome does not break blocks</li>
 *     <li>Passengers do not work again</li>
 *     <li>Well drops are broken again</li>
 * </ul>
 */
public final class LuckyBlockMod {
    public static final String NAMESPACE = "lucky";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final OutcomeManager OUTCOME_MANAGER = new OutcomeManager();

    public static final DataComponentType<Integer> LUCK = new DataComponentType.Builder<Integer>().codec(Codecs.rangedInt(-100, 100)).packetCodec(PacketCodecs.VAR_INT).build();

    public static final RegistryKey<Registry<OutcomeType>> OUTCOME_TYPES_KEY = RegistryKey.ofRegistry(new Identifier(NAMESPACE, "outcome_types"));
    public static final Registry<OutcomeType> OUTCOME_TYPES = new SimpleDefaultedRegistry<>("lucky:none", OUTCOME_TYPES_KEY, Lifecycle.stable(), false);

    public static final RegistryKey<Registry<ShapeType>> SHAPE_TYPES_KEY = RegistryKey.ofRegistry(new Identifier(NAMESPACE, "shape_types"));
    public static final Registry<ShapeType> SHAPE_TYPES = new SimpleDefaultedRegistry<>("lucky:empty", SHAPE_TYPES_KEY, Lifecycle.stable(), false);

    public static final RegistryKey<Registry<VecProviderType<?>>> POS_PROVIDER_TYPES_KEY = RegistryKey.ofRegistry(new Identifier(NAMESPACE, "pos_provider_types"));
    public static final Registry<VecProviderType<?>> POS_PROVIDER_TYPES = new SimpleDefaultedRegistry<>("lucky:zero", POS_PROVIDER_TYPES_KEY, Lifecycle.stable(), false);

    public static final Block TEST_LUCKY_BLOCK = new LuckyBlock(AbstractBlock.Settings.create().hardness(.1f).resistance(20f).mapColor(MapColor.TERRACOTTA_YELLOW));
    public static final Item TEST_LUCKY_BLOCK_ITEM = new BlockItem(TEST_LUCKY_BLOCK, new Item.Settings().rarity(Rarity.RARE).component(LUCK, 0));

    public static void init() {
        OutcomeType.init();
        ShapeType.init();
        VecProviderType.init();
        Registry.register(Registries.DATA_COMPONENT_TYPE, new Identifier(NAMESPACE, "luck"), LUCK);
        Registry.register(Registries.BLOCK, new Identifier(NAMESPACE, "test_lucky_block"), TEST_LUCKY_BLOCK);
        Registry.register(Registries.ITEM, new Identifier(NAMESPACE, "test_lucky_block"), TEST_LUCKY_BLOCK_ITEM);
    }
}
