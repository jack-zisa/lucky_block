package dev.creoii.luckyblock.util.provider.floatprovider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.ContextualProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.floatprovider.FloatProviderType;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class WorldFloatProvider extends FloatProvider implements ContextualProvider<FloatProvider> {
    public static final MapCodec<WorldFloatProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(RegistryKey.createCodec(RegistryKeys.DIMENSION_TYPE).optionalFieldOf("source").forGetter(worldIntProvider -> worldIntProvider.source),
                Value.CODEC.fieldOf("value").forGetter(worldIntProvider -> worldIntProvider.value)
        ).apply(instance, WorldFloatProvider::new);
    });
    private final Optional<RegistryKey<DimensionType>> source;
    private final Value value;
    private Outcome.Context context;

    public WorldFloatProvider(Optional<RegistryKey<DimensionType>> source, Value value) {
        this.source = source;
        this.value = value;
    }

    @Override
    public WorldFloatProvider withContext(Outcome.Context context) {
        this.context = context;
        return this;
    }

    @Override
    public float get(Random random) {
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
    public float getMin() {
        return value.min.apply(context);
    }

    @Override
    public float getMax() {
        return value.max.apply(context);
    }

    @Override
    public FloatProviderType<?> getType() {
        return LuckyFloatProviderTypes.WORLD;
    }

    @Override
    public Type getValueType() {
        return Type.FLOAT;
    }

    public enum Value implements StringIdentifiable {
        MOON_PHASE(0, 7, (key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getRainGradient(1f);
        }),
        SKY_ANGLE_RADIANS(0, 7, (key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getSkyAngleRadians(1f);
        }),
        SKY_ANGLE(0, 7, (key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getSkyAngle(1f);
        }),
        SPAWN_ANGLE(0, 7, (key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getSpawnAngle();
        }),
        THUNDER_GRADIENT(0, 7, (key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getThunderGradient(1f);
        }),
        MOON_SIZE(0, 7, (key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getMoonSize();
        }),
        PHOTOAXIS_FAVOR(0, 7, (key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getPhototaxisFavor(context1.pos());
        }),
        BRIGHTNESS(0, 15, (key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getBrightness(context1.pos());
        }),
        DISMOUNT_HEIGHT(0, 7, (key, context1) -> {
            return (float) Outcome.Context.getSourceWorld(key, context1).getDismountHeight(context1.pos());
        }),
        LOCAL_DIFFICULTY(0, 15, (key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getLocalDifficulty(context1.pos()).getLocalDifficulty();
        }),
        CLAMPED_LOCAL_DIFFICULTY(0, 15, (key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getLocalDifficulty(context1.pos()).getClampedLocalDifficulty();
        }),
        MUSIC_VOLUME(0, 15, (key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getBiome(context1.pos()).value().getMusicVolume();
        }),
        TEMPERATURE(0, 15, (key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getBiome(context1.pos()).value().getTemperature();
        }),
        AMBIENT_LIGHT(0, 15, (key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getDimension().ambientLight();
        }),
        COORDINATE_SCALE(0, 15, (key, context1) -> {
            return (float) Outcome.Context.getSourceWorld(key, context1).getDimension().coordinateScale();
        }),
        MILLIS_PER_TICK(0, Integer.MAX_VALUE, (key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getTickManager().getMillisPerTick();
        }),
        TICK_RATE(0, Integer.MAX_VALUE, (key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getTickManager().getTickRate();
        });

        public static final EnumCodec<Value> CODEC = StringIdentifiable.createCodec(Value::values);
        private final Function<Outcome.Context, Float> min;
        private final Function<Outcome.Context, Float> max;
        private final BiFunction<RegistryKey<DimensionType>, Outcome.Context, Float> function;

        Value(float min, float max, BiFunction<RegistryKey<DimensionType>, Outcome.Context, Float> function) {
            this(context -> min, context -> max, function);
        }

        Value(Function<Outcome.Context, Float> min, Function<Outcome.Context, Float> max, BiFunction<RegistryKey<DimensionType>, Outcome.Context, Float> function) {
            this.min = min;
            this.max = max;
            this.function = function;
        }

        @Override
        public String asString() {
            return name().toLowerCase();
        }
    }
}
