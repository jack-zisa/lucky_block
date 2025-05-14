package dev.creoii.luckyblock.util.provider.integer;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.ContextualProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.IntProviderType;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class WorldIntProvider extends IntProvider implements ContextualProvider<IntProvider> {
    public static final MapCodec<WorldIntProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(RegistryKey.createCodec(RegistryKeys.DIMENSION_TYPE).optionalFieldOf("source").forGetter(worldIntProvider -> worldIntProvider.source),
                Value.CODEC.fieldOf("value").forGetter(worldIntProvider -> worldIntProvider.value)
        ).apply(instance, WorldIntProvider::new);
    });
    private final Optional<RegistryKey<DimensionType>> source;
    private final Value value;
    private Outcome.Context context;

    public WorldIntProvider(Optional<RegistryKey<DimensionType>> source, Value value) {
        this.source = source;
        this.value = value;
    }

    @Override
    public WorldIntProvider withContext(Outcome.Context context) {
        this.context = context;
        return this;
    }

    @Override
    public int get(Random random) {
        if (context == null) {
            throw new IllegalStateException("Cannot get value from WorldIntProvider when context is null");
        }

        if (source.isPresent()) {
            return value.function.apply(source.orElse(DimensionTypes.OVERWORLD), context);
        } else {
            return value.function.apply(DimensionTypes.OVERWORLD, context);
        }
    }

    @Override
    public int getMin() {
        return value.min.apply(context);
    }

    @Override
    public int getMax() {
        return value.max.apply(context);
    }

    @Override
    public IntProviderType<?> getType() {
        return LuckyIntProviderTypes.WORLD;
    }

    @Override
    public Type getValueType() {
        return Type.INT;
    }

    public enum Value implements StringIdentifiable {
        MOON_PHASE(0, 7, (key, context1) -> {
            System.out.println(getSourceWorld(key, context1).getMoonPhase());
            return getSourceWorld(key, context1).getMoonPhase();
        }),
        AMBIENT_DARKNESS(0, 11, (key, context1) -> {
            return getSourceWorld(key, context1).getAmbientDarkness();
        }),
        BOTTOM_Y(context1 -> context1.world().getBottomY(), context1 -> context1.world().getBottomY(), (key, context1) -> {
            return getSourceWorld(key, context1).getBottomY();
        }),
        HEIGHT(context1 -> context1.world().getHeight(), context1 -> context1.world().getHeight(), (key, context1) -> {
            return getSourceWorld(key, context1).getHeight();
        }),
        SEA_LEVEL(context1 -> context1.world().getSeaLevel(), context1 -> context1.world().getSeaLevel(), (key, context1) -> {
            return getSourceWorld(key, context1).getSeaLevel();
        }),
        TOP_Y(context1 -> context1.world().getBottomY(), context1 -> context1.world().getBottomY() + context1.world().getHeight(), (key, context1) -> {
            return getSourceWorld(key, context1).getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, context1.pos().getX(), context1.pos().getZ());
        }),
        BASE_LIGHT_LEVEL(0, 15, (key, context1) -> {
            return getSourceWorld(key, context1).getBaseLightLevel(context1.pos(), 0);
        }),
        LIGHT_LEVEL(0, 15, (key, context1) -> {
            return getSourceWorld(key, context1).getLightLevel(context1.pos());
        }),
        LUMINANCE(0, 15, (key, context1) -> {
            return getSourceWorld(key, context1).getLuminance(context1.pos());
        });

        public static final StringIdentifiable.EnumCodec<Value> CODEC = StringIdentifiable.createCodec(Value::values);
        private final Function<Outcome.Context, Integer> min;
        private final Function<Outcome.Context, Integer> max;
        private final BiFunction<RegistryKey<DimensionType>, Outcome.Context, Integer> function;

        Value(int min, int max, BiFunction<RegistryKey<DimensionType>, Outcome.Context, Integer> function) {
            this(context -> min, context -> max, function);
        }

        Value(Function<Outcome.Context, Integer> min, Function<Outcome.Context, Integer> max, BiFunction<RegistryKey<DimensionType>, Outcome.Context, Integer> function) {
            this.min = min;
            this.max = max;
            this.function = function;
        }

        @Override
        public String asString() {
            return name().toLowerCase();
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
