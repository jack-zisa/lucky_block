package dev.creoii.luckyblock.util.provider.stringprovider;

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

public class WorldStringProvider extends StringProvider implements ContextualProvider<StringProvider> {
    public static final MapCodec<WorldStringProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(RegistryKey.createCodec(RegistryKeys.DIMENSION_TYPE).optionalFieldOf("source").forGetter(worldIntProvider -> worldIntProvider.source),
                Value.CODEC.fieldOf("value").forGetter(worldIntProvider -> worldIntProvider.value)
        ).apply(instance, WorldStringProvider::new);
    });
    private final Optional<RegistryKey<DimensionType>> source;
    private final Value value;
    private Outcome.Context context;

    public WorldStringProvider(Optional<RegistryKey<DimensionType>> source, Value value) {
        this.source = source;
        this.value = value;
    }

    @Override
    public WorldStringProvider withContext(Outcome.Context context) {
        this.context = context;
        return this;
    }

    @Override
    public String get(Random random) {
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
    public StringProviderType<?> getType() {
        return StringProviderType.WORLD;
    }

    @Override
    public Type getValueType() {
        return Type.STRING;
    }

    public enum Value implements StringIdentifiable {
        DIFFICULTY((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getDifficulty().asString();
        }),
        DIMENSION_ID((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getDimensionEntry().getIdAsString();
        }),
        BIOME_ID((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getBiome(context1.pos()).getIdAsString();
        }),
        REGISTRY_KEY((key, context1) -> {
            return Outcome.Context.getSourceWorld(key, context1).getRegistryKey().getValue().toString();
        });

        public static final EnumCodec<Value> CODEC = StringIdentifiable.createCodec(Value::values);
        private final BiFunction<RegistryKey<DimensionType>, Outcome.Context, String> function;

        Value(BiFunction<RegistryKey<DimensionType>, Outcome.Context, String> function) {
            this.function = function;
        }

        @Override
        public String asString() {
            return name().toLowerCase();
        }
    }
}
