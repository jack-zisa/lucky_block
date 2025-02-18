package dev.creoii.luckyblock.util.provider.integer;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.ContextualIntProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
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

public class WorldIntProvider extends IntProvider implements ContextualIntProvider {
    public static final MapCodec<WorldIntProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(DimensionType.REGISTRY_CODEC.optionalFieldOf("source").forGetter(worldIntProvider -> worldIntProvider.source),
                Value.CODEC.fieldOf("value").forGetter(worldIntProvider -> worldIntProvider.value)
        ).apply(instance, WorldIntProvider::new);
    });
    private final Optional<RegistryEntry<DimensionType>> source;
    private final Value value;
    private Outcome.Context context;

    public WorldIntProvider(Optional<RegistryEntry<DimensionType>> source, Value value) {
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
            return value.function.apply(source.get().getKey().orElse(DimensionTypes.OVERWORLD), context);
        } else return value.function.apply(DimensionTypes.OVERWORLD, context);
    }

    @Override
    public int getMin() {
        return value.min;
    }

    @Override
    public int getMax() {
        return value.max;
    }

    @Override
    public IntProviderType<?> getType() {
        return null;
    }

    public enum Value implements StringIdentifiable {
        MOON_PHASE(0, 1, (key, context1) -> {
            return getWorldFromSource(key, context1).getMoonPhase();
        }),
        AMBIENT_DARKNESS(0, 1, (key, context1) -> {
            return getWorldFromSource(key, context1).getAmbientDarkness();
        }),
        BOTTOM_Y(0, 1, (key, context1) -> {
            return getWorldFromSource(key, context1).getBottomY();
        }),
        HEIGHT(0, 1, (key, context1) -> {
            return getWorldFromSource(key, context1).getHeight();
        }),
        SEA_LEVEL(0, 1, (key, context1) -> {
            return getWorldFromSource(key, context1).getSeaLevel();
        }),
        TOP_Y(0, 1, (key, context1) -> {
            return getWorldFromSource(key, context1).getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, context1.pos().getX(), context1.pos().getZ());
        }),
        BASE_LIGHT_LEVEL(0, 1, (key, context1) -> {
            return getWorldFromSource(key, context1).getBaseLightLevel(context1.pos(), 0);
        }),
        LIGHT_LEVEL(0, 1, (key, context1) -> {
            return getWorldFromSource(key, context1).getLightLevel(context1.pos());
        }),
        LUMINANCE(0, 1, (key, context1) -> {
            return getWorldFromSource(key, context1).getLuminance(context1.pos());
        });

        public static final StringIdentifiable.EnumCodec<Value> CODEC = StringIdentifiable.createCodec(Value::values);
        private final int min;
        private final int max;
        private final BiFunction<RegistryKey<DimensionType>, Outcome.Context, Integer> function;

        Value(int min, int max, BiFunction<RegistryKey<DimensionType>, Outcome.Context, Integer> function) {
            this.min = min;
            this.max = max;
            this.function = function;
        }

        @Override
        public String asString() {
            return name().toLowerCase();
        }

        public static World getWorldFromSource(RegistryKey<DimensionType> source, Outcome.Context context) {
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
