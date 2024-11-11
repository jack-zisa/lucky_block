package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.LuckyBlockMod;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public record OutcomeType(MapCodec<? extends Outcome> codec) {
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
        register(Identifier.of(LuckyBlockMod.NAMESPACE, "none"), NONE);
        register(Identifier.of(LuckyBlockMod.NAMESPACE, "random"), RANDOM);
        register(Identifier.of(LuckyBlockMod.NAMESPACE, "group"), GROUP);
        register(Identifier.of(LuckyBlockMod.NAMESPACE, "message"), MESSAGE);
        register(Identifier.of(LuckyBlockMod.NAMESPACE, "command"), COMMAND);
        register(Identifier.of(LuckyBlockMod.NAMESPACE, "block"), BLOCK);
        register(Identifier.of(LuckyBlockMod.NAMESPACE, "item"), ITEM);
        register(Identifier.of(LuckyBlockMod.NAMESPACE, "entity"), ENTITY);
        register(Identifier.of(LuckyBlockMod.NAMESPACE, "feature"), FEATURE);
        register(Identifier.of(LuckyBlockMod.NAMESPACE, "structure"), STRUCTURE);
        register(Identifier.of(LuckyBlockMod.NAMESPACE, "particle"), PARTICLE);
        register(Identifier.of(LuckyBlockMod.NAMESPACE, "sound"), SOUND);
        register(Identifier.of(LuckyBlockMod.NAMESPACE, "effect"), EFFECT);
        register(Identifier.of(LuckyBlockMod.NAMESPACE, "explosion"), EXPLOSION);
    }

    private static void register(Identifier id, OutcomeType outcomeType) {
        Registry.register(LuckyBlockMod.OUTCOME_TYPES, id, outcomeType);
    }
}
