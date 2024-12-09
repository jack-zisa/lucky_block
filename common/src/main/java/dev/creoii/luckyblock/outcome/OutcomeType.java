package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.LuckyBlockMod;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public record OutcomeType(MapCodec<? extends Outcome<? extends ContextInfo>> codec) {
    public static final OutcomeType NONE = new OutcomeType(NoneOutcome.CODEC);
    public static final OutcomeType BLOCK = new OutcomeType(BlockOutcome.CODEC);
    public static final OutcomeType ITEM = new OutcomeType(ItemOutcome.CODEC);
    public static final OutcomeType ENTITY = new OutcomeType(EntityOutcome.CODEC);

    public static void init() {
        registerOutcomeType(Identifier.of(LuckyBlockMod.NAMESPACE, "none"), NONE);
        registerOutcomeType(Identifier.of(LuckyBlockMod.NAMESPACE, "block"), BLOCK);
        registerOutcomeType(Identifier.of(LuckyBlockMod.NAMESPACE, "item"), ITEM);
        registerOutcomeType(Identifier.of(LuckyBlockMod.NAMESPACE, "entity"), ENTITY);
    }

    private static void registerOutcomeType(Identifier id, OutcomeType outcomeType) {
        Registry.register(LuckyBlockMod.OUTCOME_TYPES, id, outcomeType);
    }
}
