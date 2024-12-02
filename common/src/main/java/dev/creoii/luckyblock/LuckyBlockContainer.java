package dev.creoii.luckyblock;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.block.LuckyBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.HashMap;
import java.util.Map;

public class LuckyBlockContainer {
    public static final Codec<LuckyBlockContainer> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(Identifier.CODEC.fieldOf("id").forGetter(container -> container.id),
                Codec.BOOL.fieldOf("right_click_open").orElse(true).forGetter(container -> container.rightClickOpen),
                Settings.CODEC.fieldOf("settings").orElse(Settings.DEFAULT).forGetter(container -> container.settings),
                Codec.dispatchedMap(Registries.ITEM.getCodec(), item -> Codec.INT).fieldOf("item_luck").orElse(Maps.newHashMap()).forGetter(container -> container.itemLuck)
        ).apply(instance, LuckyBlockContainer::new);
    });
    private final Identifier id;
    private final boolean rightClickOpen;
    private final Settings settings;
    private final Map<Item, Integer> itemLuck;
    private final Map<Identifier, JsonObject> randomOutcomes;
    private final Map<Identifier, JsonObject> nonrandomOutcomes;
    private LuckyBlock block;
    private BlockItem blockItem;

    public LuckyBlockContainer(Identifier id, boolean rightClickOpen, Settings settings, Map<Item, Integer> itemLuck) {
        this.id = id;
        this.rightClickOpen = rightClickOpen;
        this.settings = settings;
        this.itemLuck = itemLuck;
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

    public Map<Item, Integer> getItemLuck() {
        return itemLuck;
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

    public void addItemLuckValue(Item item, int luck) {
        itemLuck.put(item, luck);
    }

    public int getItemLuckValue(Item item) {
        return itemLuck.getOrDefault(item, 0);
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

    public record Settings(float hardness, float resistance, String rarity) {
        public static final Settings DEFAULT = new Settings(.2f, 20f, Rarity.RARE.name().toLowerCase());
        public static final Codec<Settings> CODEC = RecordCodecBuilder.create(instance -> {
            return instance.group(Codec.FLOAT.fieldOf("hardness").orElse(.1f).forGetter(settings -> settings.hardness),
                    Codec.FLOAT.fieldOf("resistance").orElse(20f).forGetter(settings -> settings.resistance),
                    Codec.STRING.fieldOf("rarity").orElse(Rarity.RARE.name().toLowerCase()).forGetter(settings -> settings.rarity)
            ).apply(instance, Settings::new);
        });
    }
}
