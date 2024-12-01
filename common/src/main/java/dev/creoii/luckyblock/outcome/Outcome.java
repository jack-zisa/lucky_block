package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.util.vec.ConstantVecProvider;
import dev.creoii.luckyblock.util.vec.VecProvider;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.function.Function;

public abstract class Outcome {
    public static final Codec<Outcome> CODEC = LuckyBlockMod.OUTCOME_TYPES.getCodec().dispatch(Outcome::getType, OutcomeType::codec);
    private final OutcomeType type;
    private final int luck;
    private final float chance;
    private final int delay;
    private final Optional<VecProvider> pos;
    private final boolean reinit;

    public Outcome(OutcomeType type) {
        this(type, 0, 1f, 0, Optional.empty(), false);
    }

    public Outcome(OutcomeType type, int luck, float chance, int delay, Optional<VecProvider> pos, boolean reinit) {
        this.type = type;
        this.luck = luck;
        this.chance = chance;
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
}
