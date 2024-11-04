package dev.creoii.luckyblock.neoforge;

import com.google.common.collect.ImmutableList;
import dev.creoii.luckyblock.LuckyBlockContainer;
import dev.creoii.luckyblock.LuckyBlockManager;

import java.util.List;
import java.util.Map;

public class NeoForgeLuckyBlockManager extends LuckyBlockManager {
    @Override
    public Map<String, LuckyBlockContainer> init() {
        return Map.of();
    }

    @Override
    public List<String> getIgnoredMods() {
        return new ImmutableList.Builder<String>()
                .add("minecraft").add("neoforge").add("architectury")
                .build();
    }
}
