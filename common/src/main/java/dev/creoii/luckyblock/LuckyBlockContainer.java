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
import net.minecraft.util.StringIdentifiable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LuckyBlockContainer {
    private static final List<Activation> DEFAULT_ACTIVATIONS = List.of(Activation.BREAK_SURVIVAL, Activation.POWER);
    public static final Codec<LuckyBlockContainer> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(Identifier.CODEC.fieldOf("id").forGetter(container -> container.id),
                Activation.CODEC.listOf().fieldOf("activations").orElse(DEFAULT_ACTIVATIONS).forGetter(container -> container.activations),
                Settings.CODEC.fieldOf("settings").orElse(Settings.DEFAULT).forGetter(container -> container.settings),
                Codec.dispatchedMap(Registries.ITEM.getCodec(), item -> Codec.INT).fieldOf("item_luck").orElse(Maps.newHashMap()).forGetter(container -> container.itemLuck),
                Codec.BOOL.fieldOf("debug").orElse(false).forGetter(container -> container.debug)
        ).apply(instance, LuckyBlockContainer::new);
    });
    private final Identifier id;
    private final List<Activation> activations;
    private final Settings settings;
    private final Map<Item, Integer> itemLuck;
    private final boolean debug;
    private final Map<Identifier, JsonObject> randomOutcomes;
    private final Map<Identifier, JsonObject> nonrandomOutcomes;
    private LuckyBlock block;
    private BlockItem blockItem;

    public LuckyBlockContainer(Identifier id, List<Activation> activations, Settings settings, Map<Item, Integer> itemLuck, boolean debug) {
        this.id = id;
        this.activations = activations;
        this.settings = settings;
        this.itemLuck = itemLuck;
        this.debug = debug;
        randomOutcomes = new HashMap<>();
        nonrandomOutcomes = new HashMap<>();

        if (activations.isEmpty()) {
            LuckyBlockMod.LOGGER.info("No activation types found for Lucky Block container: {}. It won't do anything!", id);
        }
    }

    public Identifier getId() {
        return id;
    }

    public boolean hasActivation(Activation activation) {
        return activations.contains(activation);
    }

    public Settings getSettings() {
        return settings;
    }

    public Map<Item, Integer> getItemLuck() {
        return itemLuck;
    }

    public boolean isDebug() {
        return debug;
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

    public record Settings(float hardness, float resistance, Rarity rarity) {
        public static final Settings DEFAULT = new Settings(.2f, 20f, Rarity.RARE);
        public static final Codec<Settings> CODEC = RecordCodecBuilder.create(instance -> {
            return instance.group(Codec.FLOAT.fieldOf("hardness").orElse(.1f).forGetter(settings -> settings.hardness),
                    Codec.FLOAT.fieldOf("resistance").orElse(20f).forGetter(settings -> settings.resistance),
                    Rarity.CODEC.fieldOf("rarity").orElse(Rarity.RARE).forGetter(settings -> settings.rarity)
            ).apply(instance, Settings::new);
        });
    }

    public enum Activation implements StringIdentifiable {
        BREAK_CREATIVE,
        BREAK_SURVIVAL,
        RIGHT_CLICK,
        POWER;

        public static final Codec<Activation> CODEC = StringIdentifiable.createCodec(Activation::values);

        @Override
        public String asString() {
            return name().toLowerCase();
        }
    }
}
