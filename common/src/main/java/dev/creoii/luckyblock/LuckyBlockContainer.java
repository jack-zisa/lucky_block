package dev.creoii.luckyblock;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.block.LuckyBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class LuckyBlockContainer {
    public static final Codec<LuckyBlockContainer> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(Identifier.CODEC.fieldOf("id").forGetter(container -> container.id)
        ).apply(instance, LuckyBlockContainer::new);
    });
    private final Identifier id;
    private final Map<Identifier, JsonObject> randomOutcomes;
    private final Map<Identifier, JsonObject> nonrandomOutcomes;
    private LuckyBlock block;
    private BlockItem blockItem;

    public LuckyBlockContainer(Identifier id) {
        this.id = id;
        randomOutcomes = new HashMap<>();
        nonrandomOutcomes = new HashMap<>();
    }

    public Identifier getId() {
        return id;
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
}
