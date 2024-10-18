package dev.creoii.luckyblock;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.block.LuckyBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.HashMap;
import java.util.Map;

public class LuckyBlockContainer {
    public static final Codec<LuckyBlockContainer> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(Identifier.CODEC.fieldOf("id").forGetter(container -> container.id),
                Codec.BOOL.fieldOf("right_click_open").orElse(true).forGetter(container -> container.rightClickOpen),
                Settings.CODEC.fieldOf("settings").orElse(Settings.DEFAULT).forGetter(container -> container.settings)
        ).apply(instance, LuckyBlockContainer::new);
    });
    private final Identifier id;
    private final boolean rightClickOpen;
    private final Settings settings;
    private final Map<Identifier, JsonObject> randomOutcomes;
    private final Map<Identifier, JsonObject> nonrandomOutcomes;
    private LuckyBlock block;
    private BlockItem blockItem;

    public LuckyBlockContainer(Identifier id, boolean rightClickOpen, Settings settings) {
        this.id = id;
        this.rightClickOpen = rightClickOpen;
        this.settings = settings;
        randomOutcomes = new HashMap<>();
        nonrandomOutcomes = new HashMap<>();
    }

    public Identifier getId() {
        return id;
    }

    public boolean doesRightClickOpen() {
        return rightClickOpen;
    }

    public Settings getSettings() {
        return settings;
    }

    public Map<Identifier, JsonObject> getRandomOutcomes() {
        return randomOutcomes;
    }

    public void addRandomOutcome(Identifier id, JsonObject outcome) {
        randomOutcomes.put(id, outcome);
    }

    public Map<Identifier, JsonObject> getNonrandomOutcomes() {
        return nonrandomOutcomes;
    }

    public void addNonRandomOutcome(Identifier id, JsonObject outcome) {
        nonrandomOutcomes.put(id, outcome);
    }

    public void setBlock(LuckyBlock block) {
        this.block = block;
    }

    public LuckyBlock getBlock() {
        return block;
    }

    public void setBlockItem(BlockItem blockItem) {
        this.blockItem = blockItem;
    }

    public BlockItem getBlockItem() {
        return blockItem;
    }

    public record Settings(float hardness, float resistance, Rarity rarity) {
        public static final Settings DEFAULT = new Settings(.2f, 20f, Rarity.RARE);
        public static final Codec<Settings> CODEC = RecordCodecBuilder.create(instance -> {
            return instance.group(Codec.FLOAT.fieldOf("hardness").orElse(.1f).forGetter(settings -> settings.hardness),
                    Codec.FLOAT.fieldOf("resistance").orElse(20f).forGetter(settings -> settings.resistance),
                    Rarity.CODEC.fieldOf("rarity").orElse(Rarity.RARE).forGetter(settings -> settings.rarity)
            ).apply(instance, Settings::new);
        });
    }
}
