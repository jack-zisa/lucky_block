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
import java.nio.file.*;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

public class LuckyBlockAddonsResourcePack implements ResourcePack {
    public static final Gson GSON = new Gson();
    public static final Text DESCRIPTION_TEXT = Text.translatable("pack.description.luckyBlockAddonResources");
    private static final long MAX_IN_MEMORY_FILE_SIZE = 10 * 1024 * 1024; // 10 MB
    private final ResourceType type;

    public LuckyBlockAddonsResourcePack(ResourceType type) {
        this.type = type;
    }

    public static boolean hasAcceptableFileExtension(String s) {
        return s.endsWith(".json") || s.endsWith(".png") || s.endsWith(".nbt") || s.endsWith(".png.mcmeta") || s.endsWith(".mcfunction");
    }

    public PackResourceMetadata getMetadata() {
        return new PackResourceMetadata(DESCRIPTION_TEXT, SharedConstants.getGameVersion().getResourceVersion(ResourceType.CLIENT_RESOURCES), Optional.empty());
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
                if (addonPath.equals(addonsPath))
                    continue;

                if (Files.isDirectory(addonPath)) {
                    Path assetPath = addonPath.resolve(this.type == ResourceType.SERVER_DATA ? "data" : "assets").resolve(id.getNamespace()).resolve(id.getPath());
                    if (Files.exists(assetPath)) {
                        return InputSupplier.create(assetPath);
                    }
                } else if (addonPath.toString().endsWith(".zip")) {
                    try (FileSystem fileSystem = FileSystems.newFileSystem(addonPath, (ClassLoader) null)) {
                        Path assetPath = fileSystem.getPath(this.type == ResourceType.SERVER_DATA ? "data" : "assets").resolve(id.getNamespace()).resolve(id.getPath());
                        if (Files.exists(assetPath)) {
                            long fileSize = Files.size(assetPath);
                            Path tempFile;

                            if (fileSize <= MAX_IN_MEMORY_FILE_SIZE) {
                                tempFile = Files.createTempFile("resource_", "_" + id.getPath().replace("/", "_"));
                                tempFile.toFile().deleteOnExit();
                                Files.write(tempFile, Files.readAllBytes(assetPath));
                            } else {
                                tempFile = Files.createTempFile("resource_", "_" + id.getPath().replace("/", "_"));
                                tempFile.toFile().deleteOnExit();
                                try (InputStream inputStream = Files.newInputStream(assetPath)) {
                                    Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
                                }
                            }

                            return InputSupplier.create(tempFile);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("Error processing ZIP file at " + addonPath, e);
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
                    if (Files.isDirectory(addonPath)) {
                        Path namespacePath = addonPath.resolve(this.type == ResourceType.SERVER_DATA ? "data" : "assets").resolve(namespace);
                        Path prefixPath = namespacePath.resolve(prefix);
                        String separator = addonsPath.getFileSystem().getSeparator();
                        if (Files.isDirectory(prefixPath)) {
                            try {
                                Files.walk(prefixPath).forEach(assetPath -> {
                                    if (!assetPath.equals(prefixPath) && hasAcceptableFileExtension(assetPath.toString())) {
                                        String asset = prefixPath.relativize(assetPath).toString().replace(separator, "/");
                                        consumer.accept(Identifier.of(namespace, prefix + "/" + asset), InputSupplier.create(assetPath));
                                    }
                                });
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    } else if (addonPath.toString().endsWith(".zip")) {
                        try (ZipFile zipFile = new ZipFile(addonPath.toFile())) {
                            String prefixDir = (type == ResourceType.SERVER_DATA ? "data/" : "assets/") + namespace + "/" + prefix + "/";

                            zipFile.stream().filter(entry -> !entry.isDirectory() && entry.getName().startsWith(prefixDir)).forEach(zipEntry -> {
                                        String asset = zipEntry.getName().substring(prefixDir.length());
                                        if (hasAcceptableFileExtension(asset)) {
                                            try (InputStream inputStream = zipFile.getInputStream(zipEntry)) {
                                                long fileSize = zipEntry.getSize();

                                                Path tempFile;
                                                if (fileSize <= MAX_IN_MEMORY_FILE_SIZE && fileSize > 0) {
                                                    tempFile = Files.createTempFile("resource_", "_" + asset.replace("/", "_"));
                                                    tempFile.toFile().deleteOnExit();
                                                    Files.write(tempFile, inputStream.readAllBytes());
                                                } else {
                                                    tempFile = Files.createTempFile("resource_", "_" + asset.replace("/", "_"));
                                                    tempFile.toFile().deleteOnExit();
                                                    Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
                                                }

                                                consumer.accept(Identifier.of(namespace, prefix + "/" + asset), InputSupplier.create(tempFile));
                                            } catch (IOException e) {
                                                throw new RuntimeException("Error processing ZIP entry: " + zipEntry.getName(), e);
                                            }
                                        }
                                    });
                        } catch (IOException e) {
                            throw new RuntimeException("Error accessing or processing ZIP file: " + addonPath, e);
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

    public record Factory(ResourceType type) implements ResourcePackProfile.PackFactory {
        @Override
        public ResourcePack open(ResourcePackInfo info) {
            return new LuckyBlockAddonsResourcePack(type);
        }

        @Override
        public ResourcePack openWithOverlays(ResourcePackInfo info, ResourcePackProfile.Metadata metadata) {
            return open(info);
        }
    }
}