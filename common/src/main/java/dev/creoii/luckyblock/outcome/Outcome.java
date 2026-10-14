package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.util.ContextualProvider;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import dev.creoii.luckyblock.util.vec.ConstantVecProvider;
import dev.creoii.luckyblock.util.vec.VecProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.function.Function;

public abstract class Outcome {
    public static final Codec<Outcome> CODEC = LuckyBlockMod.OUTCOME_TYPES.byNameCodec().dispatch(Outcome::getType, OutcomeType::codec);
    private final OutcomeType type;
    private final int luck;
    private final float chance;
    private final IntProvider weightProvider;
    protected final IntProvider delay;
    private final Optional<VecProvider> pos;
    private final boolean reinit;

    public Outcome(OutcomeType type) {
        this(type, 0, 1f, LuckyBlockCodecs.ONE, ConstantInt.ZERO, Optional.empty(), false);
    }

    public Outcome(OutcomeType type, int luck, float chance, IntProvider weightProvider, IntProvider delay, Optional<VecProvider> pos, boolean reinit) {
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

    public Integer getWeight(Context context) {
        IntProvider weight = this.weightProvider;
        if (weight instanceof ContextualProvider<?> contextualProvider && contextualProvider.getValueType() == ContextualProvider.Type.INT) {
            weight = (IntProvider) contextualProvider.withContext(context);
        }
        return weight.sample(context.world().getRandom());
    }

    public boolean shouldReinit() {
        return reinit;
    }

    public Integer getDelay(Context context) {
        IntProvider delay = this.delay;
        if (delay instanceof ContextualProvider<?> contextualProvider && contextualProvider.getValueType() == ContextualProvider.Type.INT) {
            delay = (IntProvider) contextualProvider.withContext(context);
        }
        return delay.sample(context.world().getRandom());
    }

    public Optional<VecProvider> getPos() {
        return pos;
    }

    public VecProvider getPos(Context context) {
        return pos.orElseGet(() -> new ConstantVecProvider(Vec3.atBottomCenterOf(context.pos())));
    }

    public static <O> RecordCodecBuilder<O, Integer> createGlobalLuckField(Function<O, Integer> getter) {
        return Codec.intRange(-2, 2).fieldOf("luck").orElse(0).forGetter(getter);
    }

    public static <O> RecordCodecBuilder<O, Float> createGlobalChanceField(Function<O, Float> getter) {
        return Codec.FLOAT.fieldOf("chance").orElse(1f).forGetter(getter);
    }

    public static <O> RecordCodecBuilder<O, IntProvider> createGlobalDelayField(Function<O, IntProvider> getter) {
        return IntProviders.NON_NEGATIVE_CODEC.fieldOf("delay").orElse(ConstantInt.ZERO).forGetter(getter);
    }

    public static <O> RecordCodecBuilder<O, IntProvider> createGlobalWeightField(Function<O, IntProvider> getter) {
        return IntProviders.POSITIVE_CODEC.fieldOf("weight").orElse(LuckyBlockCodecs.ONE).forGetter(getter);
    }

    public static <O> RecordCodecBuilder<O, Optional<VecProvider>> createGlobalPosField(Function<O, Optional<VecProvider>> getter) {
        return VecProvider.VALUE_CODEC.optionalFieldOf("pos").forGetter(getter);
    }

    public static <O> RecordCodecBuilder<O, Boolean> createGlobalReinitField(Function<O, Boolean> getter) {
        return Codec.BOOL.fieldOf("reinit").orElse(false).forGetter(getter);
    }

    public void runOutcome(Context context) {
        int delay = getDelay(context);
        if (delay == 0) {
            run(context);
        } else LuckyBlockMod.OUTCOME_MANAGER.addDelay(this, context, delay);
    }

    public abstract void run(Context context);

    public static class Context {
        private Level world;
        private BlockPos pos;
        private BlockState state;
        private Player player;

        public Context(Level world, BlockPos pos, BlockState state, Player player) {
            this.world = world;
            this.pos = pos;
            this.state = state;
            this.player = player;
        }

        public Level world() {
            return world;
        }

        public BlockPos pos() {
            return pos;
        }

        public BlockState state() {
            return state;
        }

        public Player player() {
            return player;
        }

        public Context withWorld(Level world) {
            this.world = world;
            return this;
        }

        public Context withPos(BlockPos pos) {
            this.pos = pos;
            return this;
        }

        public Context withState(BlockState state) {
            this.state = state;
            return this;
        }

        public Context withPlayer(Player player) {
            this.player = player;
            return this;
        }

        public static Level getSourceWorld(ResourceKey<Level> source, Outcome.Context context) {
            if (context.world() instanceof ServerLevel serverWorld) {
                MinecraftServer server = serverWorld.getServer();
                if (source.equals(Level.OVERWORLD)) {
                    return server.overworld();
                } else if (source == Level.NETHER) {
                    return server.getLevel(Level.NETHER);
                } else if (source == Level.END) {
                    return server.getLevel(Level.END);
                }
            }
            return context.world();
        }
    }
}
