package dev.creoii.luckyblock.outcome;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import dev.creoii.luckyblock.util.vec.ConstantVecProvider;
import dev.creoii.luckyblock.util.vec.VecProvider;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public abstract class Outcome {
    public static final Codec<Outcome> BASE_CODEC = LuckyBlockMod.OUTCOME_TYPES.getCodec().dispatch(Outcome::getType, OutcomeType::codec);
    public static final Codec<Outcome> EITHER_CODEC = Codec.either(Reference.CODEC, BASE_CODEC).xmap(either -> {
        return either.map(Reference::getOutcome, Function.identity());
    }, Either::right);
    private final OutcomeType type;
    private final int luck;
    private final float chance;
    private final IntProvider weightProvider;
    private final int delay;
    private final Optional<VecProvider> pos;
    private final boolean reinit;

    public Outcome(OutcomeType type) {
        this(type, 0, 1f, LuckyBlockCodecs.ONE, 0, Optional.empty(), false);
    }

    public Outcome(OutcomeType type, int luck, float chance, IntProvider weightProvider, int delay, Optional<VecProvider> pos, boolean reinit) {
        this.type = type;
        this.luck = luck;
        this.chance = chance;
        this.weightProvider = weightProvider;
        this.delay = delay;
        this.pos = pos;
        this.reinit = reinit;
    }

    public OutcomeType getType() {
        return type;
    }

    public int getLuck() {
        return luck;
    }

    public float getChance() {
        return chance;
    }

    public IntProvider getWeightProvider() {
        return weightProvider;
    }

    public boolean shouldReinit() {
        return reinit;
    }

    public Integer getDelay() {
        return delay;
    }

    public Optional<VecProvider> getPos() {
        return pos;
    }

    public VecProvider getPos(Context context) {
        return pos.orElseGet(() -> new ConstantVecProvider(context.pos().toCenterPos()));
    }

    public static <O> RecordCodecBuilder<O, Integer> createGlobalLuckField(Function<O, Integer> getter) {
        return Codecs.rangedInt(-2, 2).fieldOf("luck").orElse(0).forGetter(getter);
    }

    public static <O> RecordCodecBuilder<O, Float> createGlobalChanceField(Function<O, Float> getter) {
        return Codec.FLOAT.fieldOf("chance").orElse(1f).forGetter(getter);
    }

    public static <O> RecordCodecBuilder<O, Integer> createGlobalDelayField(Function<O, Integer> getter) {
        return Codec.INT.fieldOf("delay").orElse(0).forGetter(getter);
    }

    public static <O> RecordCodecBuilder<O, IntProvider> createGlobalWeightField(Function<O, IntProvider> getter) {
        return IntProvider.POSITIVE_CODEC.fieldOf("weight").orElse(LuckyBlockCodecs.ONE).forGetter(getter);
    }

    public static <O> RecordCodecBuilder<O, Optional<VecProvider>> createGlobalPosField(Function<O, Optional<VecProvider>> getter) {
        return VecProvider.VALUE_CODEC.optionalFieldOf("pos").forGetter(getter);
    }

    public static <O> RecordCodecBuilder<O, Boolean> createGlobalReinitField(Function<O, Boolean> getter) {
        return Codec.BOOL.fieldOf("reinit").orElse(false).forGetter(getter);
    }

    public void runOutcome(Context context) {
        if (getDelay() == 0) {
            run(context);
        } else LuckyBlockMod.OUTCOME_MANAGER.addDelay(this, context, getDelay());
    }

    public abstract void run(Context context);

    public record Context(World world, BlockPos pos, BlockState state, PlayerEntity player) {}

    public static class Reference extends Outcome {
        public static final Codec<Reference> CODEC = Identifier.CODEC.xmap(Reference::new, reference -> reference.id);
        private final Identifier id;
        private Context context;

        public Reference(Identifier id) {
            super(OutcomeType.NONE);
            this.id = id;
        }

        public Identifier getId() {
            return id;
        }

        public void setContext(Context context) {
            this.context = context;
        }

        @Nullable
        public Outcome getOutcome() {
            if (context == null)
                return null;
            JsonObject object = LuckyBlockMod.OUTCOME_MANAGER.getOutcomeById(id);
            if (object == null)
                return null;
            return LuckyBlockMod.OUTCOME_MANAGER.parseJsonOutcome(object, context);
        }

        @Override
        public void run(Context context) {}
    }
}
