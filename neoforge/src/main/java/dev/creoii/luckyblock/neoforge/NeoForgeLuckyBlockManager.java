package dev.creoii.luckyblock.neoforge;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.creoii.luckyblock.LuckyBlockContainer;
import dev.creoii.luckyblock.LuckyBlockManager;
import dev.creoii.luckyblock.LuckyBlockMod;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class NeoForgeLuckyBlockManager extends LuckyBlockManager {
    @Override
    public Path getGameDirectory() {
        return FMLLoader.getGamePath();
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

        ModList.get().getModFiles().forEach(modFileInfo -> {
            if (!getIgnoredMods().contains(modFileInfo.moduleName())) {
                try {
                    Path root = modFileInfo.getFile().getSecureJar().getPath("data");
                    if (Files.exists(root)) {
                        Files.walk(root).forEach(path -> tryLoadAddon(path, builder, LoadType.MOD));
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Failed to load resources from JAR: " + modFileInfo.getFile().getFilePath(), e);
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
                    dataResult.resultOrPartial(error -> LuckyBlockMod.LOGGER.error("Error parsing lucky block container: {}", error)).ifPresent(container -> {
                        if (!builder.build().containsKey(container.getId().getNamespace())) {
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
                .add("minecraft").add("neoforge").add("architectury")
                .build();
    }
}
