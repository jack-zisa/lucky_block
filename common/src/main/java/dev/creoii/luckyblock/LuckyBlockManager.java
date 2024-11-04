package dev.creoii.luckyblock;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class LuckyBlockManager {
    public static final Pattern PATH_PATTERN = Pattern.compile("^/data/[a-z0-9_/]+/lucky_block\\.json$");
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

    public Block[] getAllBlocks() {
        return luckyBlocks.values().stream().map(LuckyBlockContainer::getBlock).collect(Collectors.toSet()).toArray(new Block[]{});
    }

    public Item[] getAllItems() {
        return luckyBlocks.values().stream().map(LuckyBlockContainer::getBlockItem).collect(Collectors.toSet()).toArray(new Item[]{});
    }
}
