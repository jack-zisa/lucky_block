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
import dev.creoii.luckyblock.util.resource.LuckyBlockAddonsResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class NeoForgeLuckyBlockManager extends LuckyBlockManager {
    @Override
    public Path getGameDirectory() {
        System.out.println(FMLLoader.getGamePath().toString());
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
            Files.walk(getAddonsPath()).forEach(path -> tryLoadAddon(getAddonsPath().relativize(path), builder, true));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ModList.get().getModFiles().forEach(modFileInfo -> {
            if (!getIgnoredMods().contains(modFileInfo.moduleName())) {
                try {
                    Path root = modFileInfo.getFile().getSecureJar().getPath("data");
                    if (Files.exists(root)) {
                        Files.walk(root).forEach(path -> tryLoadAddon(path, builder, false));
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Failed to load resources from JAR: " + modFileInfo.getFile().getFilePath(), e);
                }
            }
        });
        return builder.build();
    }

    @Override
    public void tryLoadAddon(Path path, ImmutableMap.Builder<String, LuckyBlockContainer> builder, boolean fromAddon) {
        if (fromAddon ? ADDON_PATH_PATTERN.matcher(path.toString()).matches() : PATH_PATTERN.matcher(path.toString()).matches()) {
            try {
                String file = Files.readString(fromAddon ? getAddonsPath().resolve(path) : path);
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
    public InputStream getIcon() {
        Optional<? extends ModContainer> optional = ModList.get().getModContainerById(LuckyBlockMod.NAMESPACE);
        if (optional.isPresent()) {
            System.out.println(optional.get().getModInfo().getLogoFile());
        }
        /*Optional<Path> path = ;
        if (path.isPresent()) {
            try {
                return Files.newInputStream(path.get());
            } catch (IOException e) {
                LuckyBlockMod.LOGGER.error("Error loading built-in resource pack icon.");
            }
        }*/
        return null;
    }

    @Override
    public ResourcePackProfile createResourcePack() {
        return ResourcePackProfile.create(
                "lucky_block",
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
                .add("minecraft").add("neoforge").add("architectury")
                .build();
    }
}
