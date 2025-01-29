package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.LuckyBlockRegistries;
import dev.creoii.luckyblock.function.target.Target;
import dev.creoii.luckyblock.function.target.VecTarget;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import dev.creoii.luckyblock.util.vecprovider.ConstantVecProvider;
import dev.creoii.luckyblock.util.vecprovider.VecProvider;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class Outcome<T extends ContextInfo> {
    public static final Codec<Outcome<? extends ContextInfo>> CODEC = LuckyBlockRegistries.OUTCOME_TYPES.getCodec().dispatch(Outcome::getType, OutcomeType::codec);
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

    public Integer getDelay() {
        return delay;
    }

    public Optional<VecProvider> getPos() {
        return pos;
    }

    public VecProvider getPos(Context<T> context) {
        return pos.orElseGet(() -> new ConstantVecProvider(context.pos().toCenterPos()));
    }

    public boolean shouldReinit() {
        return reinit;
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

    public void runOutcome(Context<T> context) {
        Context<T> context1 = create(context);
        if (getDelay() == 0) {
            run(context1);
        } else LuckyBlockMod.OUTCOME_MANAGER.addDelay(this, context1, getDelay());
    }

    /**
     * Prepare objects used in {@link Outcome#run(Context)}.
     */
    public abstract Context<T> create(Context<T> context);

    /**
     * Execute outcome.
     */
    public abstract void run(Context<T> context);

    @SuppressWarnings("unchecked")
    public void runOutcomeUnchecked(Context<?> context) {
        runOutcome((Context<T>) context);
    }

    @SuppressWarnings("unchecked")
    public void runUnchecked(Context<?> context) {
        run((Context<T>) context);
    }

    public static class Context<T extends ContextInfo> implements VecTarget<Context<T>> {
        private final World world;
        private BlockPos pos;
        private final BlockState state;
        private final LivingEntity source;
        @Nullable
        private T info;

        public Context(World world, BlockPos pos, BlockState state, LivingEntity source, @Nullable T info) {
            this.world = world;
            this.pos = pos;
            this.state = state;
            this.source = source;
            this.info = info;
        }

        public World world() {
            return world;
        }

        public BlockPos pos() {
            return pos;
        }

        public BlockState state() {
            return state;
        }

        public LivingEntity source() {
            return source;
        }

        public Random random() {
            return world.getRandom();
        }

        public T info() {
            return info;
        }

        @Override
        public Target<Context<T>> update(dev.creoii.luckyblock.function.Function<Target<?>> function, Object newObject) {
            return this;
        }

        @Override
        public Context<T> setVec(Outcome<? extends ContextInfo> outcome, Context<? extends ContextInfo> context, Vec3d vec3d) {
            this.pos = BlockPos.ofFloored(vec3d.x + .5d, vec3d.y + .5d, vec3d.z + .5d);
            return this;
        }

        public Context<T> withInfo(T info) {
            this.info = info;
            return this;
        }

        public Context<T> copyFiltered(List<Target<?>> toKeep) {
            if (info == null) {
                throw new IllegalStateException("Attempted copying outcome context before creating info.");
            }
            Context<T> context = new Context<>(world, pos, state, source, info);
            toKeep.add(context);
            context.info.setTargets(toKeep);
            return context;
        }
    }
}
