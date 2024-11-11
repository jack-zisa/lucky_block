package dev.creoii.luckyblock;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class LuckyBlockManager {
    public static final Pattern PATH_PATTERN = Pattern.compile("^/?data/[^/]+/lucky_block\\.json$");
    private final Map<String, LuckyBlockContainer> luckyBlocks;

    public LuckyBlockManager() {
        luckyBlocks = init();
    }

    public abstract Map<String, LuckyBlockContainer> init();

    public abstract List<String> getIgnoredMods();

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
