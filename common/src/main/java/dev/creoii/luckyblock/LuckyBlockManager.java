package dev.creoii.luckyblock;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class LuckyBlockManager {
    public static final Pattern PATH_PATTERN = Pattern.compile("^/?data/[^/]+/lucky_block\\.json$");
    public static final Pattern ADDON_PATH_PATTERN = Pattern.compile("^[^/]+\\\\data\\\\[^\\\\]+\\\\lucky_block\\.json$");
    private final Map<String, LuckyBlockContainer> luckyBlocks;

    public LuckyBlockManager() {
        luckyBlocks = init();
    }

    public abstract Path getGameDirectory();

    public abstract Map<String, LuckyBlockContainer> init();

    public abstract void tryLoadAddon(Path path, ImmutableMap.Builder<String, LuckyBlockContainer> builder, LoadType loadType);

    public abstract List<String> getIgnoredMods();

    public Path getAddonsPath() {
        return getGameDirectory().resolve("addons");
    }

    public void tryLoadZipAddon(Path zipPath, ImmutableMap.Builder<String, LuckyBlockContainer> builder) throws IOException {
        try (FileSystem fileSystem = FileSystems.newFileSystem(zipPath, (ClassLoader) null)) {
            Path root = fileSystem.getPath("/");
            Files.walk(root).forEach(path -> {
                if (ADDON_PATH_PATTERN.matcher(path.toString()).matches() || PATH_PATTERN.matcher(path.toString()).matches()) {
                    tryLoadAddon(path, builder, LoadType.ZIP_ADDON);
                }
            });
        }
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

    public enum LoadType {
        ZIP_ADDON(PATH_PATTERN),
        FILE_ADDON(ADDON_PATH_PATTERN),
        MOD(PATH_PATTERN);

        private final Pattern pattern;

        LoadType(Pattern pattern) {
            this.pattern = pattern;
        }

        public boolean test(String s) {
            return pattern.matcher(s).matches();
        }
    }
}
