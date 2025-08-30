package dev.creoii.luckyblock.util.nbt;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.vec.VecProvider;
import net.minecraft.nbt.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class ContextualNbtCompound extends NbtCompound {
    public static final Codec<ContextualNbtCompound> CODEC = Codec.PASSTHROUGH.comapFlatMap(dynamic -> {
        NbtElement nbtElement = dynamic.convert(NbtOps.INSTANCE).getValue();
        if (nbtElement instanceof NbtCompound nbtCompound) {
            ContextualNbtCompound contextualNbtCompound = new ContextualNbtCompound().copyFrom(nbtCompound);
            DataResult<ContextualNbtCompound> result = DataResult.success(contextualNbtCompound == dynamic.getValue() ? contextualNbtCompound.copy() : contextualNbtCompound);
            return result.map(nbtCompound1 -> new ContextualNbtCompound(nbtCompound1.entries, null));
        } else return DataResult.error(() -> "Not a compound tag: " + nbtElement);
    }, nbt -> new Dynamic<>(NbtOps.INSTANCE, nbt.copy()));
    @Nullable
    private Outcome.Context context;

    protected ContextualNbtCompound(Map<String, NbtElement> entries, @Nullable Outcome.Context context) {
        super(entries);
        this.context = context;
    }

    public ContextualNbtCompound() {
        this(Maps.newHashMap(), null);
    }

    public void setContext(@Nullable Outcome.Context context) {
        this.context = context;
    }

    public @Nullable Outcome.Context getContext() {
        return context;
    }

    /**
     * Converts any byte types into int types, so the element can be parsed into an IntProvider.
     */
    private void clean(String key, JsonElement element) {
        if (element.isJsonObject()) {
            Map<String, JsonElement> map = ((JsonObject) element).asMap();
            ((NbtCompound) entries.get(key)).entries.forEach((s, nbtElement) -> {
                if (nbtElement instanceof NbtByte nbtByte) {
                    map.replace(s, new JsonPrimitive(nbtByte.intValue()));
                }
            });
        }
    }

    public Optional<Integer> getInt(String key) {
        try {
            NbtElement element = get(key);
            if (contains(key) && element != null && element.getType() == 3) {
                return Optional.of(((AbstractNbtNumber) entries.get(key)).intValue());
            } else if (element != null && element.getType() == 10 && context != null) {
                JsonElement element1 = JsonParser.parseString(entries.get(key).toString());
                clean(key, element1);
                DataResult<IntProvider> dataResult = IntProvider.VALUE_CODEC.parse(JsonOps.INSTANCE, element1);
                Optional<IntProvider> intProvider = dataResult.resultOrPartial(string -> LuckyBlockMod.LOGGER.error("Error parsing int provider: {}", string));
                if (intProvider.isPresent()) {
                    return Optional.of(intProvider.get().get(context.world().getRandom()));
                }
            }
        } catch (ClassCastException ignored) {}

        return Optional.empty();
    }

    public Optional<Short> getShort(String key) {
        try {
            NbtElement element = get(key);
            if (contains(key) && element != null && element.getType() == 2) {
                return Optional.of(((AbstractNbtNumber) entries.get(key)).shortValue());
            } else if (element != null && element.getType() == 10 && context != null) {
                JsonElement element1 = JsonParser.parseString(entries.get(key).toString());
                clean(key, element1);
                DataResult<IntProvider> dataResult = IntProvider.createValidatingCodec(-32768, 32767).parse(JsonOps.INSTANCE, element1);
                Optional<IntProvider> intProvider = dataResult.resultOrPartial(string -> LuckyBlockMod.LOGGER.error("Error parsing int provider: {}", string));
                if (intProvider.isPresent()) {
                    return Optional.of((short) intProvider.get().get(context.world().getRandom()));
                }
            }
        } catch (ClassCastException ignored) {}

        return Optional.empty();
    }

    public Optional<Byte> getByte(String key) {
        try {
            NbtElement element = get(key);
            if (contains(key) && element != null && element.getType() == 1) {
                return Optional.of(((AbstractNbtNumber) entries.get(key)).byteValue());
            } else if (element != null && element.getType() == 10 && context != null) {
                JsonElement element1 = JsonParser.parseString(entries.get(key).toString());
                clean(key, element1);
                DataResult<IntProvider> dataResult = IntProvider.VALUE_CODEC.parse(JsonOps.INSTANCE, element1);
                Optional<IntProvider> intProvider = dataResult.resultOrPartial(string -> LuckyBlockMod.LOGGER.error("Error parsing int provider: {}", string));
                if (intProvider.isPresent()) {
                    return Optional.of((byte) intProvider.get().get(context.world().getRandom()));
                }
            }
        } catch (ClassCastException ignored) {}

        return Optional.empty();
    }

    public Optional<Long> getLong(String key) {
        try {
            NbtElement element = get(key);
            if (contains(key) && element != null && element.getType() == 4) {
                return Optional.of(((AbstractNbtNumber) entries.get(key)).longValue());
            } else if (element != null && element.getType() == 10 && context != null) {
                JsonElement element1 = JsonParser.parseString(entries.get(key).toString());
                clean(key, element1);
                DataResult<IntProvider> dataResult = IntProvider.VALUE_CODEC.parse(JsonOps.INSTANCE, element1);
                Optional<IntProvider> intProvider = dataResult.resultOrPartial(string -> LuckyBlockMod.LOGGER.error("Error parsing int provider: {}", string));
                if (intProvider.isPresent()) {
                    return Optional.of((long) intProvider.get().get(context.world().getRandom()));
                }
            }
        } catch (ClassCastException ignored) {}

        return Optional.empty();
    }

    public Optional<Float> getFloat(String key) {
        try {
            NbtElement element = get(key);
            if (contains(key) && element != null && element.getType() == 5) {
                return Optional.of(((AbstractNbtNumber) entries.get(key)).floatValue());
            } else if (element != null && element.getType() == 10 && context != null) {
                DataResult<FloatProvider> dataResult = FloatProvider.VALUE_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(entries.get(key).toString()));
                Optional<FloatProvider> floatProvider = dataResult.resultOrPartial(string -> LuckyBlockMod.LOGGER.error("Error parsing float provider: {}", string));
                if (floatProvider.isPresent()) {
                    return Optional.of(floatProvider.get().get(context.world().getRandom()));
                }
            }
        } catch (ClassCastException ignored) {}

        return Optional.empty();
    }

    public Optional<Double> getDouble(String key) {
        try {
            NbtElement element = get(key);
            if (contains(key) && element != null && element.getType() == 6) {
                return Optional.of(((AbstractNbtNumber) entries.get(key)).doubleValue());
            } else if (element != null && element.getType() == 10 && context != null) {
                DataResult<FloatProvider> dataResult = FloatProvider.VALUE_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(entries.get(key).toString()));
                Optional<FloatProvider> floatProvider = dataResult.resultOrPartial(string -> LuckyBlockMod.LOGGER.error("Error parsing float provider: {}", string));
                if (floatProvider.isPresent()) {
                    return Optional.of((double) floatProvider.get().get(context.world().getRandom()));
                }
            }
        } catch (ClassCastException ignored) {}

        return Optional.empty();
    }

    public Optional<int[]> getIntArray(String key) {
        try {
            NbtElement element = get(key);
            if (contains(key) && element != null && element.getType() == 11) {
                return Optional.of(((NbtIntArray) entries.get(key)).getIntArray());
            } else if (element != null && element.getType() == 10 && context != null) {
                DataResult<VecProvider> dataResult = VecProvider.VALUE_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(entries.get(key).toString()));
                Optional<VecProvider> vecProvider = dataResult.resultOrPartial(string -> LuckyBlockMod.LOGGER.error("Error parsing vec provider: {}", string));
                if (vecProvider.isPresent()) {
                    BlockPos pos = vecProvider.get().getPos(context);
                    return Optional.of(new int[]{pos.getX(), pos.getY(), pos.getZ()});
                }
            }
        } catch (ClassCastException ignored) {}

        return Optional.empty();
    }

    public Optional<long[]> getLongArray(String key) {
        try {
            NbtElement element = get(key);
            if (contains(key) && element != null && element.getType() == 12) {
                return Optional.of(((NbtLongArray) entries.get(key)).getLongArray());
            } else if (element != null && element.getType() == 10 && context != null) {
                DataResult<VecProvider> dataResult = VecProvider.VALUE_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(entries.get(key).toString()));
                Optional<VecProvider> vecProvider = dataResult.resultOrPartial(string -> LuckyBlockMod.LOGGER.error("Error parsing vec provider: {}", string));
                if (vecProvider.isPresent()) {
                    BlockPos pos = vecProvider.get().getPos(context);
                    return Optional.of(new long[]{pos.getX(), pos.getY(), pos.getZ()});
                }
            }
        } catch (ClassCastException ignored) {}

        return Optional.empty();
    }

    @Override
    public Optional<NbtCompound> getCompound(String key) {
        Optional<NbtCompound> compound = super.getCompound(key);
        return compound.map(nbtCompound -> new ContextualNbtCompound().copyFrom(nbtCompound));
    }

    public ContextualNbtList getList(String key, int type) {
        try {
            NbtElement element = get(key);
            if (element != null && element.getType() == 9) {
                ContextualNbtList nbtList = new ContextualNbtList().copyFrom((NbtList) entries.get(key));
                if (!nbtList.isEmpty() && nbtList.getType() != type) {
                    ContextualNbtList list = new ContextualNbtList();
                    list.setContext(context);
                    return list;
                }
                nbtList.setContext(context);
                return nbtList;
            } else if (element != null && element.getType() == 10 && context != null) {
                DataResult<VecProvider> dataResult = VecProvider.VALUE_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(entries.get(key).toString()));
                Optional<VecProvider> vecProvider = dataResult.resultOrPartial(string -> LuckyBlockMod.LOGGER.error("Error parsing vec provider: {}", string));
                if (vecProvider.isPresent()) {
                    ContextualNbtList nbtList = new ContextualNbtList();
                    nbtList.setContext(context);
                    Vec3d vec3d = vecProvider.get().getVec(context);
                    nbtList.add(NbtDouble.of(vec3d.x));
                    nbtList.add(NbtDouble.of(vec3d.y));
                    nbtList.add(NbtDouble.of(vec3d.z));
                    return nbtList;
                }
            }
        } catch (ClassCastException ignored) {}

        ContextualNbtList list = new ContextualNbtList();
        list.setContext(context);
        return list;
    }

    protected ContextualNbtCompound shallowCopy() {
        return new ContextualNbtCompound(new HashMap<>(entries), context);
    }

    public ContextualNbtCompound copy() {
        Map<String, NbtElement> map = Maps.newHashMap(Maps.transformValues(entries, NbtElement::copy));
        return new ContextualNbtCompound(map, context);
    }

    public static ContextualNbtCompound copyWith(NbtCompound compound, Outcome.Context context) {
        Map<String, NbtElement> map = Maps.newHashMap(Maps.transformValues(compound.entries, NbtElement::copy));
        return new ContextualNbtCompound(map, context);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContextualNbtCompound that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(context, that.context);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), context);
    }

    @Override
    public ContextualNbtCompound copyFrom(NbtCompound source) {
        if (source instanceof ContextualNbtCompound contextual) {
            setContext(contextual.getContext());
        }
        return (ContextualNbtCompound) super.copyFrom(source);
    }
}