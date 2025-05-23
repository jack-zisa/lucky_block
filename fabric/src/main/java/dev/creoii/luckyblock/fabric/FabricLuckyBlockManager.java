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
    public Path getGameDirectory() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public Map<String, LuckyBlockContainer> init() {
        ImmutableMap.Builder<String, LuckyBlockContainer> builder = ImmutableMap.builder();

        if (Files.notExists(getAddonsPath())) {
            try {
                Files.createDirectory(getAddonsPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            Files.walk(getAddonsPath(), 1).forEach(addonPath -> {
                if (Files.isDirectory(addonPath)) {
                    try {
                        Files.walk(addonPath, 3).forEach(path -> tryLoadAddon(getAddonsPath().relativize(path), builder, LoadType.FILE_ADDON));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else if (addonPath.toString().endsWith(".zip")) {
                    try {
                        tryLoadZipAddon(addonPath, builder);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to process zip file: " + addonPath, e);
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FabricLoader.getInstance().getAllMods().forEach(modContainer -> {
            if (!getIgnoredMods().contains(modContainer.getMetadata().getId()) && !modContainer.getMetadata().getId().startsWith("fabric-")) {
                Path root = modContainer.findPath("data").orElse(null);
                if (root != null) {
                    try {
                        Files.walk(root, 4).forEach(path -> tryLoadAddon(path, builder, LoadType.MOD));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        return builder.build();
    }

    @Override
    public void tryLoadAddon(Path path, ImmutableMap.Builder<String, LuckyBlockContainer> builder, LoadType loadType) {
        if (loadType.test(path.toString())) {
            try {
                String file = Files.readString(loadType == LoadType.FILE_ADDON ? getAddonsPath().resolve(path) : path);
                JsonElement element = JsonParser.parseString(file);
                if (element.isJsonObject()) {
                    DataResult<LuckyBlockContainer> dataResult = LuckyBlockContainer.CODEC.parse(JsonOps.INSTANCE, element);
                    dataResult.resultOrPartial(string -> LuckyBlockMod.LOGGER.error("Error parsing lucky block container: {}", string)).ifPresent(container -> {
                        if (!builder.build().containsKey(container.getId().getNamespace())) {
                            AbstractBlock.Settings blockSettings = AbstractBlock.Settings.create().hardness(container.getSettings().hardness()).resistance(container.getSettings().resistance()).mapColor(MapColor.TERRACOTTA_YELLOW);
                            Item.Settings itemSettings = new Item.Settings().rarity(container.getSettings().rarity());

                            container.setBlock(Registry.register(Registries.BLOCK, container.getId(), new LuckyBlock(container.getId().getNamespace(), blockSettings)));
                            container.setBlockItem(Registry.register(Registries.ITEM, container.getId(), new BlockItem(container.getBlock(), itemSettings.component(LuckyBlockMod.LUCK_COMPONENT, 0))));

                            builder.put(container.getId().getNamespace(), container);
                            if (container.isDebug())
                                LuckyBlockMod.LOGGER.info("Loaded lucky block container '{}'", container.getId().getNamespace());
                        } else {
                            LuckyBlockMod.LOGGER.error("Attempted loading two lucky block containers with the same id: {}", container.getId().getNamespace());
                        }
                    });
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public List<String> getIgnoredMods() {
        return new ImmutableList.Builder<String>()
                .add("java").add("minecraft").add("c").add("architectury").add("mixinextras").add("fabricloader")
                .build();
    }
}
