package dev.creoii.luckyblock.util.resource;

import com.google.gson.Gson;
import dev.creoii.luckyblock.LuckyBlockMod;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourceType;
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
    private static final Text DESCRIPTION_TEXT = Text.translatable("pack.description.luckyBlockAddonResources");

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
        Path addonsPath = FabricLoader.getInstance().getGameDir().resolve("addons");
        try {
            for (Path addonPath : Files.walk(addonsPath, 1).toList()) {
                if (!addonPath.equals(addonsPath)) {
                    Path assetPath = addonPath.resolve("assets").resolve(id.getNamespace()).resolve(id.getPath());
                    if (Files.exists(assetPath)) {
                        System.out.println("asset: " + assetPath);
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
        Path addonsPath = FabricLoader.getInstance().getGameDir().resolve("addons");
        try {
            Files.walk(addonsPath, 1).forEach(addonPath -> {
                if (!addonPath.equals(addonsPath)) {
                    Path namespacePath = addonPath.resolve("assets").resolve(namespace);
                    Path prefixPath = namespacePath.resolve(prefix);
                    String separator = addonsPath.getFileSystem().getSeparator();
                    if (Files.isDirectory(prefixPath)) {
                        try {
                            Files.walk(prefixPath).forEach(assetPath -> {
                                if (!assetPath.equals(prefixPath) && assetPath.toString().endsWith(".json")) {
                                    String asset = prefixPath.relativize(assetPath).toString().replace(separator, "/");
                                    System.out.println(namespace + " " + prefix + " asset: " + asset.substring(0, asset.indexOf('.')));
                                    consumer.accept(Identifier.of(namespace, asset.substring(0, asset.indexOf('.'))), InputSupplier.create(assetPath));
                                }
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
    public String getName() {
        return LuckyBlockMod.NAMESPACE;
    }

    @Override
    public void close() {}

    public record Factory() implements ResourcePackProfile.PackFactory {
        @Override
        public ResourcePack open(String name) {
            return new LuckyBlockAddonsResourcePack();
        }

        @Override
        public ResourcePack openWithOverlays(String name, ResourcePackProfile.Metadata metadata) {
            return open(name);
        }
    }
}
