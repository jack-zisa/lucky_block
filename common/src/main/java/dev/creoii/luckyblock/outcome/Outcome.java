package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;
import java.util.function.Function;

public abstract class Outcome {
    public static final Codec<Outcome> CODEC = LuckyBlockMod.OUTCOME_TYPES.getCodec().dispatch(Outcome::getType, OutcomeType::codec);
    private final OutcomeType type;
    private final Optional<Integer> delay;
    private final Optional<String> pos;

    public Outcome(OutcomeType type) {
        this(type, Optional.of(0), Optional.empty());
    }

    public Outcome(OutcomeType type, Optional<Integer> delay, Optional<String> pos) {
        this.type = type;
        this.delay = delay;
        this.pos = pos;
    }

    public OutcomeType getType() {
        return type;
    }

    public Optional<Integer> getDelay() {
        return delay;
    }

    public Optional<String> getPos() {
        return pos;
    }

    public BlockPos getPos(OutcomeContext context) {
        return getPos().isPresent() ? context.parseBlockPos(getPos().get()) : context.pos();
    }

    public Vec3d getVec(OutcomeContext context) {
        return getPos().isPresent() ? context.parseVec3d(getPos().get()) : context.pos().toCenterPos();
    }

    public static <O> RecordCodecBuilder<O, Optional<Integer>> createGlobalDelayField(Function<O, Optional<Integer>> getter) {
        return Codec.INT.optionalFieldOf("delay").forGetter(getter);
    }

    public static <O> RecordCodecBuilder<O, Optional<String>> createGlobalPosField(Function<O, Optional<String>> getter) {
        return LuckyBlockCodecs.BLOCK_POS.optionalFieldOf("pos").forGetter(getter);
    }

    public void runOutcome(OutcomeContext context) {
        if (getDelay().orElse(0) == 0) {
            run(context);
        } else LuckyBlockMod.OUTCOME_MANAGER.addDelay(this, context, getDelay().orElse(0));
    }

    public abstract void run(OutcomeContext context);
}
