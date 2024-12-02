package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.Codec;
import dev.creoii.luckyblock.LuckyBlockMod;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public record OutcomeType(Codec<? extends Outcome> codec) {
    public static final OutcomeType NONE = new OutcomeType(NoneOutcome.CODEC);
    public static final OutcomeType RANDOM = new OutcomeType(RandomOutcome.CODEC);
    public static final OutcomeType GROUP = new OutcomeType(GroupOutcome.CODEC);
    public static final OutcomeType MESSAGE = new OutcomeType(MessageOutcome.CODEC);
    public static final OutcomeType COMMAND = new OutcomeType(CommandOutcome.CODEC);
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
        registerOutcomeType(new Identifier(LuckyBlockMod.NAMESPACE, "none"), NONE);
        registerOutcomeType(new Identifier(LuckyBlockMod.NAMESPACE, "random"), RANDOM);
        registerOutcomeType(new Identifier(LuckyBlockMod.NAMESPACE, "group"), GROUP);
        registerOutcomeType(new Identifier(LuckyBlockMod.NAMESPACE, "message"), MESSAGE);
        registerOutcomeType(new Identifier(LuckyBlockMod.NAMESPACE, "command"), COMMAND);
        registerOutcomeType(new Identifier(LuckyBlockMod.NAMESPACE, "block"), BLOCK);
        registerOutcomeType(new Identifier(LuckyBlockMod.NAMESPACE, "item"), ITEM);
        registerOutcomeType(new Identifier(LuckyBlockMod.NAMESPACE, "entity"), ENTITY);
        registerOutcomeType(new Identifier(LuckyBlockMod.NAMESPACE, "feature"), FEATURE);
        registerOutcomeType(new Identifier(LuckyBlockMod.NAMESPACE, "structure"), STRUCTURE);
        registerOutcomeType(new Identifier(LuckyBlockMod.NAMESPACE, "particle"), PARTICLE);
        registerOutcomeType(new Identifier(LuckyBlockMod.NAMESPACE, "sound"), SOUND);
        registerOutcomeType(new Identifier(LuckyBlockMod.NAMESPACE, "effect"), EFFECT);
        registerOutcomeType(new Identifier(LuckyBlockMod.NAMESPACE, "explosion"), EXPLOSION);
    }

    private static void registerOutcomeType(Identifier id, OutcomeType outcomeType) {
        Registry.register(LuckyBlockMod.OUTCOME_TYPES, id, outcomeType);
    }
}
