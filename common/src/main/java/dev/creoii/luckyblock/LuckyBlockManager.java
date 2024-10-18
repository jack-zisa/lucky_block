package dev.creoii.luckyblock;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.creoii.luckyblock.block.LuckyBlock;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LuckyBlockManager {
    private static final Pattern PATH_PATTERN = Pattern.compile("^/data/[a-z0-9_/]+/lucky_blocks/[a-z0-9_/]+\\.json$");
    private static final List<String> IGNORED_MODS = new ImmutableList.Builder<String>()
            .add("java").add("minecraft").add("c").add("architectury").add("mixinextras").add("fabric-api").add("fabricloader")
            .build();
    private Map<String, LuckyBlockContainer> luckyBlocks;

    public LuckyBlockManager() {
        init();
    }

    @Nullable
    public LuckyBlockContainer getContainer(String namespace) {
        return luckyBlocks.getOrDefault(namespace, null);
    }

    private void init() {
        ImmutableMap.Builder<String, LuckyBlockContainer> builder = ImmutableMap.builder();
        FabricLoader.getInstance().getAllMods().forEach(modContainer -> {
            if (!IGNORED_MODS.contains(modContainer.getMetadata().getId())) {
                Path root = modContainer.findPath("data").orElse(null);
                if (root != null) {
                    try {
                        Files.walk(root).forEach(path -> {
                            if (PATH_PATTERN.matcher(path.toString()).matches()) {
                                try {
                                    String file = Files.readString(path);
                                    JsonElement element = JsonParser.parseString(file);
                                    if (element.isJsonObject()) {
                                        DataResult<Identifier> dataResult = Identifier.CODEC.parse(JsonOps.INSTANCE, ((JsonObject) element).get("id"));
                                        dataResult.resultOrPartial().ifPresent(identifier -> {
                                            LuckyBlockMod.LOGGER.info("Loading lucky block container '{}'", identifier.getNamespace());
                                            LuckyBlockContainer container = new LuckyBlockContainer(identifier);
                                            container.setBlock(Registry.register(Registries.BLOCK, identifier, new LuckyBlock(identifier.getNamespace(), AbstractBlock.Settings.create().hardness(.1f).resistance(20f).mapColor(MapColor.TERRACOTTA_YELLOW))));
                                            container.setBlockItem(Registry.register(Registries.ITEM, identifier, new BlockItem(container.getBlock(), new Item.Settings().rarity(Rarity.RARE).component(LuckyBlockMod.LUCK, 0))));
                                            builder.put(identifier.getNamespace(), container);
                                        });
                                    }
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        luckyBlocks = builder.build();
    }

    public Block[] getAllBlocks() {
        return luckyBlocks.values().stream().map(LuckyBlockContainer::getBlock).collect(Collectors.toSet()).toArray(new Block[]{});
    }

    public Item[] getAllItems() {
        return luckyBlocks.values().stream().map(LuckyBlockContainer::getBlockItem).collect(Collectors.toSet()).toArray(new Item[]{});
    }
}
