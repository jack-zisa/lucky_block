package dev.creoii.luckyblock;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class LuckyBlockManager {
    public static final Path ADDONS_PATH = MinecraftClient.getInstance().runDirectory.toPath().resolve("addons");
    public static final Pattern PATH_PATTERN = Pattern.compile("^/?data/[^/]+/lucky_block\\.json$");
    public static final Pattern ADDON_PATH_PATTERN = Pattern.compile("^[^/]+\\\\data\\\\[^\\\\]+\\\\lucky_block\\.json$");
    private final Map<String, LuckyBlockContainer> luckyBlocks;

    public LuckyBlockManager() {
        luckyBlocks = init();
    }

    public abstract Map<String, LuckyBlockContainer> init();

    public abstract void tryLoadAddon(Path path, ImmutableMap.Builder<String, LuckyBlockContainer> builder, boolean fromAddon);

    public abstract List<String> getIgnoredMods();

    public Map<Identifier, JsonElement> loadOutcomes(Map<Identifier, JsonElement> prepared, ResourceManager resourceManager) {
        try {
            Files.walk(ADDONS_PATH, 1).forEach(path -> {
                if (!path.equals(ADDONS_PATH)) {
                    Path datapackPath = path.resolve("data");
                    try {
                        Files.walk(datapackPath, 1).forEach(dataPath -> {
                            if (!dataPath.equals(datapackPath)) {
                                String namespace = datapackPath.relativize(dataPath).toString();
                                Path outcomesPath = dataPath.resolve("outcomes");
                                try {
                                    Files.walk(outcomesPath).forEach(outcomePath -> {
                                        if (!outcomePath.equals(outcomesPath)) {
                                            if (Files.isRegularFile(outcomePath)) {
                                                try {
                                                    String file = Files.readString(outcomePath);
                                                    JsonElement element = JsonParser.parseString(file);
                                                    if (element.isJsonObject()) {
                                                        String outcome = outcomesPath.relativize(outcomePath).toString().replace(".json", "");
                                                        LuckyBlockContainer container = LuckyBlockMod.luckyBlockManager.getContainer(namespace);
                                                        if (container != null) {
                                                            if (container.isDebug())
                                                                LuckyBlockMod.LOGGER.info("Loading outcome '{}'", outcome);

                                                            if (outcome.startsWith("nonrandom/")) {
                                                                container.addNonRandomOutcome(Identifier.of(namespace, outcome), (JsonObject) element);
                                                            } else
                                                                container.addRandomOutcome(Identifier.of(namespace, outcome), (JsonObject) element);
                                                        }
                                                    }
                                                } catch (IOException e) {
                                                    LuckyBlockMod.LOGGER.error("Error loading outcome '{}' for addon: {}: {}", outcomePath, datapackPath, e.toString());
                                                }
                                            }
                                        }
                                    });
                                } catch (IOException e) {
                                    LuckyBlockMod.LOGGER.error("Error loading outcomes for addon: {}: {}", datapackPath, e.toString());
                                }
                            }
                        });
                    } catch (IOException e) {
                        LuckyBlockMod.LOGGER.error("Error finding data for addon: {}: {}", datapackPath, e.toString());
                    }
                }
            });
        } catch (IOException e) {
            LuckyBlockMod.LOGGER.error("Error finding addons: {}", e.toString());
        }
        return prepared;
    }

    @Nullable
    public LuckyBlockContainer getContainer(String namespace) {
        return luckyBlocks.getOrDefault(namespace, null);
    }

    public LuckyBlockContainer[] getAllContainers() {
        return luckyBlocks.values().toArray(new LuckyBlockContainer[0]);
    }

    public Block[] getAllBlocks() {
        return luckyBlocks.values().stream().map(LuckyBlockContainer::getBlock).distinct().toArray(Block[]::new);
    }

    public Item[] getAllItems() {
        return luckyBlocks.values().stream().map(LuckyBlockContainer::getBlockItem).distinct().toArray(Item[]::new);
    }
}
