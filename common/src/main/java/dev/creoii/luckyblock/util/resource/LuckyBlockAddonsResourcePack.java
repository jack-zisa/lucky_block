package dev.creoii.luckyblock.util.resource;

import com.google.gson.Gson;
import dev.creoii.luckyblock.LuckyBlockMod;
import net.minecraft.SharedConstants;
import net.minecraft.resource.*;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataMap;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class LuckyBlockAddonsResourcePack implements ResourcePack {
    public static final Gson GSON = new Gson();
    protected static final Text DESCRIPTION_TEXT = Text.translatable("pack.description.luckyBlockAddonResources");
    private final ResourceType resourceType;

    public LuckyBlockAddonsResourcePack(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public static boolean hasAcceptableFileExtension(String s) {
        return s.endsWith(".json") || s.endsWith(".png") || s.endsWith(".nbt") || s.endsWith(".png.mcmeta");
    }

    public PackResourceMetadata getMetadata() {
        return new PackResourceMetadata(DESCRIPTION_TEXT, SharedConstants.getGameVersion().getResourceVersion(resourceType), Optional.empty());
    }

    @Nullable
    @Override
    public InputSupplier<InputStream> openRoot(String... segments) {
        if (segments.length > 0) {
            switch (segments[0]) {
                case "pack.mcmeta":
                    return () -> {
                        String metadata = GSON.toJson(PackResourceMetadata.SERIALIZER.toJson(getMetadata()));
                        return IOUtils.toInputStream(metadata, StandardCharsets.UTF_8);
                    };
                case "pack.png":
                    return LuckyBlockAddonsResourcePackCreator::getDefaultIcon;
            }
        }

        return null;
    }

    @Nullable
    @Override
    public InputSupplier<InputStream> open(ResourceType type, Identifier id) {
        Path addonsPath = LuckyBlockMod.luckyBlockManager.getAddonsPath();
        try {
            for (Path addonPath : Files.walk(addonsPath, 1).toList()) {
                if (!addonPath.equals(addonsPath)) {
                    Path assetPath = addonPath.resolve(type == ResourceType.SERVER_DATA ? "data" : "assets").resolve(id.getNamespace()).resolve(id.getPath());
                    if (Files.exists(assetPath)) {
                        return InputSupplier.create(assetPath);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void findResources(ResourceType type, String namespace, String prefix, ResultConsumer consumer) {
        Path addonsPath = LuckyBlockMod.luckyBlockManager.getAddonsPath();
        try {
            Files.walk(addonsPath, 1).forEach(addonPath -> {
                if (!addonPath.equals(addonsPath)) {
                    Path namespacePath = addonPath.resolve(type == ResourceType.SERVER_DATA ? "data" : "assets").resolve(namespace);
                    Path prefixPath = namespacePath.resolve(prefix);
                    String separator = addonsPath.getFileSystem().getSeparator();
                    if (Files.isDirectory(prefixPath)) {
                        try {
                            Files.walk(prefixPath).forEach(assetPath -> {
                                if (!assetPath.equals(prefixPath) && hasAcceptableFileExtension(assetPath.toString())) {
                                    String asset = prefixPath.relativize(assetPath).toString().replace(separator, "/");
                                    consumer.accept(Identifier.of(namespace, prefix + "/" + asset), InputSupplier.create(assetPath));                                }
                            });
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<String> getNamespaces(ResourceType type) {
        return Arrays.stream(LuckyBlockMod.luckyBlockManager.getAllContainers()).map(container -> container.getId().getNamespace()).collect(Collectors.toSet());
    }

    @Nullable
    @Override
    public <T> T parseMetadata(ResourceMetadataReader<T> metaReader) {
        return ResourceMetadataMap.of(PackResourceMetadata.SERIALIZER, getMetadata()).get(metaReader);
    }

    @Override
    public ResourcePackInfo getInfo() {
        return LuckyBlockAddonsResourcePackCreator.RESOURCE_PACK_INFO;
    }

    @Override
    public String getId() {
        return LuckyBlockMod.NAMESPACE;
    }

    @Override
    public void close() {}

    public record Factory(ResourceType resourceType) implements ResourcePackProfile.PackFactory {
        @Override
        public ResourcePack open(ResourcePackInfo info) {
            return new LuckyBlockAddonsResourcePack(resourceType);
        }

        @Override
        public ResourcePack openWithOverlays(ResourcePackInfo info, ResourcePackProfile.Metadata metadata) {
            return open(info);
        }
    }
}