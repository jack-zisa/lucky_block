package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.LuckyBlockMod;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

public record OutcomeType(MapCodec<? extends Outcome> codec) {
    public static final OutcomeType NONE = new OutcomeType(NoneOutcome.CODEC);
    public static final OutcomeType RANDOM = new OutcomeType(RandomOutcome.CODEC);
    public static final OutcomeType GROUP = new OutcomeType(GroupOutcome.CODEC);
    public static final OutcomeType MESSAGE = new OutcomeType(MessageOutcome.CODEC);
    public static final OutcomeType COMMAND = new OutcomeType(CommandOutcome.CODEC);
    public static final OutcomeType DIFFICULTY = new OutcomeType(DifficultyOutcome.CODEC);
    public static final OutcomeType ADVANCEMENT = new OutcomeType(AdvancementOutcome.CODEC);
    public static final OutcomeType BLOCK = new OutcomeType(BlockOutcome.CODEC);
    public static final OutcomeType ITEM = new OutcomeType(ItemOutcome.CODEC);
    public static final OutcomeType ENTITY = new OutcomeType(EntityOutcome.CODEC);
    public static final OutcomeType FEATURE = new OutcomeType(FeatureOutcome.CODEC);
    public static final OutcomeType STRUCTURE = new OutcomeType(StructureOutcome.CODEC);
    public static final OutcomeType PARTICLE = new OutcomeType(ParticleOutcome.CODEC);
    public static final OutcomeType SOUND = new OutcomeType(SoundOutcome.CODEC);
    public static final OutcomeType EFFECT = new OutcomeType(EffectOutcome.CODEC);
    public static final OutcomeType EXPLOSION = new OutcomeType(ExplosionOutcome.CODEC);

    public static void init() {
        registerOutcomeType(Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "none"), NONE);
        registerOutcomeType(Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "random"), RANDOM);
        registerOutcomeType(Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "group"), GROUP);
        registerOutcomeType(Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "message"), MESSAGE);
        registerOutcomeType(Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "command"), COMMAND);
        registerOutcomeType(Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "difficulty"), DIFFICULTY);
        registerOutcomeType(Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "advancement"), ADVANCEMENT);
        registerOutcomeType(Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "block"), BLOCK);
        registerOutcomeType(Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "item"), ITEM);
        registerOutcomeType(Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "entity"), ENTITY);
        registerOutcomeType(Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "feature"), FEATURE);
        registerOutcomeType(Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "structure"), STRUCTURE);
        registerOutcomeType(Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "particle"), PARTICLE);
        registerOutcomeType(Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "sound"), SOUND);
        registerOutcomeType(Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "effect"), EFFECT);
        registerOutcomeType(Identifier.fromNamespaceAndPath(LuckyBlockMod.NAMESPACE, "explosion"), EXPLOSION);
    }

    private static void registerOutcomeType(Identifier id, OutcomeType outcomeType) {
        Registry.register(LuckyBlockMod.OUTCOME_TYPES, id, outcomeType);
    }
}
