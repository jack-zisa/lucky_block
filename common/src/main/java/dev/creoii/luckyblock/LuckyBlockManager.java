package dev.creoii.luckyblock;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.resource.*;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class LuckyBlockManager {
    /**
     * This won't work on servers!
     */
    public static final Pattern PATH_PATTERN = Pattern.compile("^/?data/[^/]+/lucky_block\\.json$");
    public static final Pattern ADDON_PATH_PATTERN = Pattern.compile("^[^/]+\\\\data\\\\[^\\\\]+\\\\lucky_block\\.json$");
    public static final ResourcePackSource RESOURCE_PACK_SOURCE = new ResourcePackSource() {
        @Override
        public Text decorate(Text packName) {
            return Text.translatable("pack.nameAndSource", packName, Text.translatable("pack.source.luckyBlockAddon"));
        }

        @Override
        public boolean canBeEnabledLater() {
            return true;
        }
    };
    private final Map<String, LuckyBlockContainer> luckyBlocks;

    public LuckyBlockManager() {
        luckyBlocks = init();
    }

    public abstract Path getGameDirectory();

    public abstract Map<String, LuckyBlockContainer> init();

    public abstract void tryLoadAddon(Path path, ImmutableMap.Builder<String, LuckyBlockContainer> builder, boolean fromAddon);

    public abstract ResourcePackProfile createResourcePack();

    public abstract InputStream getIcon();

    public abstract List<String> getIgnoredMods();

    public Path getAddonsPath() {
        return getGameDirectory().resolve("addons");
    }

    public Map<Identifier, JsonElement> loadOutcomes(Map<Identifier, JsonElement> prepared, ResourceManager resourceManager) {
        try {
            Files.walk(getAddonsPath(), 1).forEach(path -> {
                if (!path.equals(getAddonsPath())) {
                    Path datapackPath = path.resolve("data");
                    try {
                        Files.walk(datapackPath, 1).forEach(dataPath -> {
                            if (!dataPath.equals(datapackPath)) {
                                String namespace = datapackPath.relativize(dataPath).toString();
                                Path outcomesPath = dataPath.resolve("outcomes");
                                String separator = getAddonsPath().getFileSystem().getSeparator();
                                try {
                                    Files.walk(outcomesPath).forEach(outcomePath -> {
                                        if (!outcomePath.equals(outcomesPath)) {
                                            if (Files.isRegularFile(outcomePath)) {
                                                try {
                                                    String file = Files.readString(outcomePath);
                                                    JsonElement element = JsonParser.parseString(file);
                                                    if (element.isJsonObject()) {
                                                        String outcome = outcomesPath.relativize(outcomePath).toString().replace(".json", "").replace(separator, "/");
                                                        LuckyBlockContainer container = LuckyBlockMod.luckyBlockManager.getContainer(namespace);
                                                        if (container != null) {
                                                            if (container.isDebug())
                                                                LuckyBlockMod.LOGGER.info("Loading outcome '{}'", outcome);

                                                            if (outcome.startsWith("nonrandom/")) {
                                                                container.addNonRandomOutcome(Identifier.of(namespace, outcome), element.getAsJsonObject());
                                                            } else
                                                                container.addRandomOutcome(Identifier.of(namespace, outcome), element.getAsJsonObject());
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
