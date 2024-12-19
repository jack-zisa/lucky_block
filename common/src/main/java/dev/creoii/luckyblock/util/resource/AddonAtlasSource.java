package dev.creoii.luckyblock.util.resource;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.LuckyBlockMod;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.texture.atlas.AtlasSource;
import net.minecraft.client.texture.atlas.AtlasSourceType;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class AddonAtlasSource implements AtlasSource {
    public static final MapCodec<AddonAtlasSource> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(Codec.STRING.fieldOf("source").forGetter(source -> source.source), Codec.STRING.fieldOf("prefix").forGetter(source -> source.prefix)).apply(instance, AddonAtlasSource::new);
    });
    private final String source;
    private final String prefix;

    public AddonAtlasSource(String source, String prefix) {
        this.source = source;
        this.prefix = prefix;
    }

    @Override
    public void load(ResourceManager resourceManager, SpriteRegions regions) {
        for (String namespace : resourceManager.getAllNamespaces()) {
            Path addonsPath = FabricLoader.getInstance().getGameDir().resolve("addons");
            try {
                Files.walk(addonsPath, 1).forEach(addonPath -> {
                    if (!addonPath.equals(addonsPath)) {
                        Path namespacePath = addonPath.resolve("assets").resolve(namespace);
                        Path resourcesPath = namespacePath.resolve("textures\\" + source);
                        if (Files.exists(resourcesPath)) {
                            try {
                                Files.walk(resourcesPath).forEach(resourcePath -> {
                                    if (!resourcePath.equals(resourcesPath) && (resourcePath.toString().endsWith(".json") || resourcePath.toString().endsWith(".png"))) {
                                        Identifier id = Identifier.of(namespace, "textures/" + source + "/" + resourcesPath.relativize(resourcePath));
                                        System.out.println("relative id: " + id.toString());
                                        Optional<ResourcePack> optionalResourcePack = resourceManager.streamResourcePacks().filter(resourcePack -> resourcePack.getId().equals(LuckyBlockMod.NAMESPACE)).findFirst();
                                        System.out.println("pack present? " + optionalResourcePack.isPresent());

                                        optionalResourcePack.ifPresent(resourcePack -> {
                                            System.out.println("found pack " + LuckyBlockMod.NAMESPACE);
                                            Resource texture = new Resource(resourcePack, InputSupplier.create(resourcePath));
                                            regions.add(id, texture);
                                        });
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
    }

    @Override
    public AtlasSourceType getType() {
        return LuckyBlockMod.ADDON_ATLAS_SOURCE;
    }
}