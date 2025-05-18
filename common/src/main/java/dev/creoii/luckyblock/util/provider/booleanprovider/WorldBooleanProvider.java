package dev.creoii.luckyblock.util.provider.booleanprovider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.ContextualProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.Optional;
import java.util.function.BiFunction;

public class WorldBooleanProvider extends BooleanProvider implements ContextualProvider<BooleanProvider> {
    public static final MapCodec<WorldBooleanProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(RegistryKey.createCodec(RegistryKeys.DIMENSION_TYPE).optionalFieldOf("source").forGetter(worldIntProvider -> worldIntProvider.source),
                Value.CODEC.fieldOf("value").forGetter(worldIntProvider -> worldIntProvider.value)
        ).apply(instance, WorldBooleanProvider::new);
    });
    private final Optional<RegistryKey<DimensionType>> source;
    private final Value value;
    private Outcome.Context context;

    public WorldBooleanProvider(Optional<RegistryKey<DimensionType>> source, Value value) {
        this.source = source;
        this.value = value;
    }

    @Override
    public WorldBooleanProvider withContext(Outcome.Context context) {
        this.context = context;
        return this;
    }

    @Override
    public boolean get(Random random) {
        if (context == null) {
            throw new IllegalStateException("Cannot get value from WorldFloatProvider when context is null");
        }

        if (source.isPresent()) {
            return value.function.apply(source.orElse(DimensionTypes.OVERWORLD), context);
        } else {
            return value.function.apply(DimensionTypes.OVERWORLD, context);
        }
    }

    @Override
    public BooleanProviderType<?> getType() {
        return BooleanProviderType.WORLD;
    }

    @Override
    public Type getValueType() {
        return Type.BOOLEAN;
    }

    public enum Value implements StringIdentifiable {
        CAN_SET_ICE((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getBiome(context1.pos()).value().canSetIce(context1.world(), context1.pos());
        }),
        CAN_SET_SNOW((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getBiome(context1.pos()).value().canSetSnow(context1.world(), context1.pos());
        }),
        DOES_NOT_SNOW((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getBiome(context1.pos()).value().doesNotSnow(context1.pos(), context1.world().getSeaLevel());
        }),
        HAS_PRECIPITATION((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getBiome(context1.pos()).value().hasPrecipitation();
        }),
        IS_COLD((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getBiome(context1.pos()).value().isCold(context1.pos(), context1.world().getSeaLevel());
        }),
        SHOULD_GENERATE_LOWER_FROZEN_OCEAN_SURFACE((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getBiome(context1.pos()).value().shouldGenerateLowerFrozenOceanSurface(context1.pos(), context1.world().getSeaLevel());
        }),
        BED_WORKS((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getDimension().bedWorks();
        }),
        HAS_CEILING((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getDimension().hasCeiling();
        }),
        HAS_FIXED_TIME((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getDimension().hasFixedTime();
        }),
        HAS_RAIDS((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getDimension().hasRaids();
        }),
        HAS_SKY_LIGHT((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getDimension().hasSkyLight();
        }),
        NATURAL((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getDimension().natural();
        }),
        PIGLIN_SAFE((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getDimension().piglinSafe();
        }),
        RESPAWN_ANCHOR_WORKS((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getDimension().respawnAnchorWorks();
        }),
        ULTRAWARM((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getDimension().ultrawarm();
        }),
        IS_DIFFICULTY_LOCKED((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getLevelProperties().isDifficultyLocked();
        }),
        IS_HARDCORE((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getLevelProperties().isHardcore();
        }),
        IS_RAINING((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getLevelProperties().isRaining();
        }),
        IS_THUNDERING((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getLevelProperties().isThundering();
        }),
        IS_CHUNK_LOADED((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getChunkManager().isChunkLoaded(context1.pos().getX(), context1.pos().getZ());
        }),
        HAS_BELOW_ZERO_RETROGEN((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getChunk(context1.pos()).hasBelowZeroRetrogen();
        }),
        IS_LIGHT_ON((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getChunk(context1.pos()).isLightOn();
        }),
        HAS_STRUCTURE_REFERENCES((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getChunk(context1.pos()).hasStructureReferences();
        }),
        IS_SERIALIZABLE((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getChunk(context1.pos()).isSerializable();
        }),
        NEEDS_SAVING((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getChunk(context1.pos()).needsSaving();
        }),
        USES_OLD_NOISE((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getChunk(context1.pos()).usesOldNoise();
        }),
        HAS_LIGHTING_UPDATES((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getLightingProvider().hasUpdates();
        }),
        IS_WATER((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).isWater(context1.pos());
        }),
        IS_CLIENT((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).isClient();
        }),
        HAS_RAIN((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).hasRain(context1.pos());
        }),
        IS_DAY((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).isDay();
        }),
        IS_DEBUG_WORLD((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).isDebugWorld();
        }),
        IS_IN_BUILD_LIMIT((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).isInBuildLimit(context1.pos());
        }),
        IS_NIGHT((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).isNight();
        }),
        SHOULD_TICK_BLOCK_POS((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).shouldTickBlockPos(context1.pos());
        }),
        IS_AIR((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).isAir(context1.pos());
        }),
        IS_IN_HEIGHT_LIMIT((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).isInHeightLimit(context1.pos().getY());
        }),
        IS_OUT_OF_HEIGHT_LIMIT((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).isOutOfHeightLimit(context1.pos().getY());
        }),
        SHOULD_TICK((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getTickManager().shouldTick();
        }),
        IS_TICK_FROZEN((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getTickManager().isFrozen();
        }),
        IS_TICK_STEPPING((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getTickManager().isStepping();
        }),
        IS_AT_LEAST_HARD((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getLocalDifficulty(context1.pos()).isAtLeastHard();
        });

        public static final EnumCodec<Value> CODEC = StringIdentifiable.createCodec(Value::values);
        private final BiFunction<RegistryKey<DimensionType>, Outcome.Context, Boolean> function;

        Value(BiFunction<RegistryKey<DimensionType>, Outcome.Context, Boolean> function) {
            this.function = function;
        }

        @Override
        public String asString() {
            return name().toLowerCase();
        }
    }
}
