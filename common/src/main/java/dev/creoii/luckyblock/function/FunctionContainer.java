package dev.creoii.luckyblock.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.creoii.luckyblock.LuckyBlockRegistries;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class FunctionContainer {
    public static final FunctionContainer EMPTY = new FunctionContainer(Reference2ObjectMaps.emptyMap());
    public static final Codec<FunctionContainer> CODEC = Codec.dispatchedMap(Type.CODEC, Type::getValueCodec).xmap(map -> {
        if (map.isEmpty()) {
            return EMPTY;
        } else {
            Reference2ObjectMap<FunctionType, Optional<Function<?>>> functions = new Reference2ObjectArrayMap<>(map.size());
            for (Map.Entry<Type, Function<?>> entry : map.entrySet()) {
                functions.put(entry.getKey().type(), Optional.of(entry.getValue()));
            }
            return new FunctionContainer(functions);
        }
    }, functions -> {
        Reference2ObjectMap<Type, Function<?>> map = new Reference2ObjectArrayMap<>(functions.functions.size());
        for (Reference2ObjectMap.Entry<FunctionType, Optional<Function<?>>> entry : Reference2ObjectMaps.fastIterable(functions.functions)) {
            entry.getValue().ifPresent(o -> map.put(new Type(entry.getKey()), o));
        }
        return map;
    });
    private final Reference2ObjectMap<FunctionType, Optional<Function<?>>> functions;

    public FunctionContainer(Reference2ObjectMap<FunctionType, Optional<Function<?>>> functions) {
        this.functions = functions;
    }

    public boolean has(FunctionType type) {
        return functions.containsKey(type);
    }

    public void forEach(Consumer<Function<?>> action) {
        functions.forEach((functionType, function) -> {
            function.ifPresent(action);
        });
    }

    public void forEach(Consumer<Function<?>> action, Predicate<Function<?>> predicate) {
        functions.forEach((functionType, function) -> {
            function.ifPresent(f -> {
                if (predicate.test(f))
                    action.accept(f);
            });
        });
    }

    public static class Builder {
        private final Reference2ObjectMap<FunctionType, Optional<Function<?>>> changes = new Reference2ObjectArrayMap<>();

        public Builder addAll(FunctionContainer functionContainer) {
            functionContainer.forEach(this::add);
            return this;
        }

        public Builder add(FunctionType type, Function<?> value) {
            this.changes.put(type, Optional.of(value));
            return this;
        }

        public Builder add(Function<?> component) {
            return this.add(component.getType(), component);
        }

        public FunctionContainer build() {
            return this.changes.isEmpty() ? FunctionContainer.EMPTY : new FunctionContainer(this.changes);
        }
    }

    private record Type(FunctionType type) {
        public static final Codec<Type> CODEC = Codec.STRING.flatXmap(id -> {
            Identifier identifier = Identifier.tryParse(id);
            FunctionType componentType = LuckyBlockRegistries.FUNCTION_TYPES.get(identifier);
            if (componentType == null) {
                return DataResult.error(() -> {
                    return "No function with type: '" + identifier + "'";
                });
            } else {
                return DataResult.success(new Type(componentType));
            }
        }, type -> {
            FunctionType functionType = type.type();
            Identifier identifier = LuckyBlockRegistries.FUNCTION_TYPES.getId(functionType);
            return identifier == null ? DataResult.error(() -> {
                return "Unregistered function type: " + functionType;
            }) : DataResult.success(identifier.toString());
        });

        @SuppressWarnings("unchecked")
        public Codec<Function<?>> getValueCodec() {
            return (Codec<Function<?>>) this.type.codec().codec();
        }
    }
}
