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
import dev.creoii.luckyblock.block.LuckyBlockItem;
import dev.creoii.luckyblock.util.resource.LuckyBlockAddonsResourcePack;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.MapColor;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FabricLuckyBlockManager extends LuckyBlockManager {
    @Override
    public Map<String, LuckyBlockContainer> init() {
        ImmutableMap.Builder<String, LuckyBlockContainer> builder = ImmutableMap.builder();

        if (Files.notExists(ADDONS_PATH)) {
            try {
                Files.createDirectory(ADDONS_PATH);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            Files.walk(ADDONS_PATH, 4).forEach(path -> tryLoadAddon(ADDONS_PATH.relativize(path), builder, true));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FabricLoader.getInstance().getAllMods().forEach(modContainer -> {
            if (!getIgnoredMods().contains(modContainer.getMetadata().getId()) && !modContainer.getMetadata().getId().startsWith("fabric-")) {
                Path root = modContainer.findPath("data").orElse(null);
                if (root != null) {
                    try {
                        Files.walk(root, 4).forEach(path -> tryLoadAddon(path, builder, false));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        return builder.build();
    }

    @Override
    public void tryLoadAddon(Path path, ImmutableMap.Builder<String, LuckyBlockContainer> builder, boolean fromAddon) {
        if (fromAddon ? ADDON_PATH_PATTERN.matcher(path.toString()).matches() : PATH_PATTERN.matcher(path.toString()).matches()) {
            try {
                String file = Files.readString(fromAddon ? ADDONS_PATH.resolve(path) : path);
                JsonElement element = JsonParser.parseString(file);
                if (element.isJsonObject()) {
                    DataResult<LuckyBlockContainer> dataResult = LuckyBlockContainer.CODEC.parse(JsonOps.INSTANCE, element);
                    dataResult.resultOrPartial(string -> LuckyBlockMod.LOGGER.error("Error parsing lucky block container: {}", string)).ifPresent(container -> {
                        if (!builder.build().containsKey(container.getId().getNamespace())) {
                            AbstractBlock.Settings blockSettings = AbstractBlock.Settings.create().hardness(container.getSettings().hardness()).resistance(container.getSettings().resistance()).mapColor(MapColor.TERRACOTTA_YELLOW);
                            Item.Settings itemSettings = new Item.Settings().rarity(Rarity.valueOf(container.getSettings().rarity().toUpperCase()));

                            container.setBlock(Registry.register(Registries.BLOCK, container.getId(), new LuckyBlock(container.getId().getNamespace(), blockSettings)));
                            container.setBlockItem(Registry.register(Registries.ITEM, container.getId(), new LuckyBlockItem(container.getBlock(), itemSettings)));

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
    public InputStream getIcon() {
        Optional<Path> path = FabricLoader.getInstance().getModContainer(LuckyBlockMod.NAMESPACE).flatMap(container -> container.getMetadata().getIconPath(512).flatMap(container::findPath));
        if (path.isPresent()) {
            try {
                return Files.newInputStream(path.get());
            } catch (IOException e) {
                LuckyBlockMod.LOGGER.error("Error loading built-in resource pack icon.");
            }
        }
        return null;
    }

    @Override
    public ResourcePackProfile createResourcePack() {
        return ResourcePackProfile.create(
                LuckyBlockMod.NAMESPACE,
                Text.translatable("pack.name.luckyBlockAddons"),
                true,
                new LuckyBlockAddonsResourcePack.Factory(),
                ResourceType.CLIENT_RESOURCES,
                ResourcePackProfile.InsertionPosition.TOP,
                RESOURCE_PACK_SOURCE
        );
    }

    @Override
    public List<String> getIgnoredMods() {
        return new ImmutableList.Builder<String>()
                .add("java").add("minecraft").add("c").add("architectury").add("mixinextras").add("fabricloader")
                .build();
    }
}
