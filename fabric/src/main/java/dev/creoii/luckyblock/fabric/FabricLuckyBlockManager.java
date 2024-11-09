package dev.creoii.luckyblock.fabric;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.creoii.luckyblock.LuckyBlockContainer;
import dev.creoii.luckyblock.LuckyBlockManager;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.block.LuckyBlock;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.MapColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class FabricLuckyBlockManager extends LuckyBlockManager {
    @Override
    public Map<String, LuckyBlockContainer> init() {
        ImmutableMap.Builder<String, LuckyBlockContainer> builder = ImmutableMap.builder();
        FabricLoader.getInstance().getAllMods().forEach(modContainer -> {
            if (!getIgnoredMods().contains(modContainer.getMetadata().getId())) {
                Path root = modContainer.findPath("data").orElse(null);
                if (root != null) {
                    try {
                        Files.walk(root).forEach(path -> {
                            if (PATH_PATTERN.matcher(path.toString()).matches()) {
                                try {
                                    String file = Files.readString(path);
                                    JsonElement element = JsonParser.parseString(file);
                                    if (element.isJsonObject()) {
                                        DataResult<LuckyBlockContainer> dataResult = LuckyBlockContainer.CODEC.parse(JsonOps.INSTANCE, element);
                                        dataResult.resultOrPartial(string -> LuckyBlockMod.LOGGER.error("Error parsing lucky block container: {}", string)).ifPresent(container -> {
                                            LuckyBlockMod.LOGGER.info("Loading lucky block container '{}'", container.getId().getNamespace());

                                            AbstractBlock.Settings blockSettings = AbstractBlock.Settings.create().hardness(container.getSettings().hardness()).resistance(container.getSettings().resistance()).mapColor(MapColor.TERRACOTTA_YELLOW);
                                            Item.Settings itemSettings = new Item.Settings().rarity(container.getSettings().rarity());

                                            container.setBlock(Registry.register(Registries.BLOCK, container.getId(), new LuckyBlock(container.getId().getNamespace(), blockSettings)));
                                            container.setBlockItem(Registry.register(Registries.ITEM, container.getId(), new BlockItem(container.getBlock(), itemSettings.component(LuckyBlockMod.LUCK_COMPONENT, 0))));

                                            builder.put(container.getId().getNamespace(), container);
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
        return builder.build();
    }

    @Override
    public List<String> getIgnoredMods() {
        return new ImmutableList.Builder<String>()
                .add("java").add("minecraft").add("c").add("architectury").add("mixinextras").add("fabric-api").add("fabricloader")
                .build();
    }
}
