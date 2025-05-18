package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.util.ContextualProvider;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import dev.creoii.luckyblock.util.vec.ConstantVecProvider;
import dev.creoii.luckyblock.util.vec.VecProvider;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.Optional;
import java.util.function.Function;

public abstract class Outcome {
    public static final Codec<Outcome> CODEC = LuckyBlockMod.OUTCOME_TYPES.getCodec().dispatch(Outcome::getType, OutcomeType::codec);
    private final OutcomeType type;
    private final int luck;
    private final float chance;
    private final IntProvider weightProvider;
    protected final IntProvider delay;
    private final Optional<VecProvider> pos;
    private final boolean reinit;

    public Outcome(OutcomeType type) {
        this(type, 0, 1f, LuckyBlockCodecs.ONE, ConstantIntProvider.ZERO, Optional.empty(), false);
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
        return weight.get(context.world().getRandom());
    }

    public boolean shouldReinit() {
        return reinit;
    }

    public Integer getDelay(Context context) {
        IntProvider delay = this.delay;
        if (delay instanceof ContextualProvider<?> contextualProvider && contextualProvider.getValueType() == ContextualProvider.Type.INT) {
            delay = (IntProvider) contextualProvider.withContext(context);
        }
        return delay.get(context.world().getRandom());
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

    public static <O> RecordCodecBuilder<O, IntProvider> createGlobalDelayField(Function<O, IntProvider> getter) {
        return IntProvider.NON_NEGATIVE_CODEC.fieldOf("delay").orElse(ConstantIntProvider.ZERO).forGetter(getter);
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
        int delay = getDelay(context);
        if (delay == 0) {
            run(context);
        } else LuckyBlockMod.OUTCOME_MANAGER.addDelay(this, context, delay);
    }

    public abstract void run(Context context);

    public static class Context {
        private World world;
        private BlockPos pos;
        private BlockState state;
        private PlayerEntity player;

        public Context(World world, BlockPos pos, BlockState state, PlayerEntity player) {
            this.world = world;
            this.pos = pos;
            this.state = state;
            this.player = player;
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

        public PlayerEntity player() {
            return player;
        }

        public Context withWorld(World world) {
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

        public Context withPlayer(PlayerEntity player) {
            this.player = player;
            return this;
        }

        public static World getSourceWorld(RegistryKey<DimensionType> source, Outcome.Context context) {
            if (context.world() instanceof ServerWorld serverWorld) {
                MinecraftServer server = serverWorld.getServer();
                if (source == DimensionTypes.OVERWORLD) {
                    return server.getOverworld();
                } else if (source == DimensionTypes.THE_NETHER) {
                    return server.getWorld(World.NETHER);
                } else if (source == DimensionTypes.THE_END) {
                    return server.getWorld(World.END);
                }
            }
            return context.world();
        }
    }
}
